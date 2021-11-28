package com.qminder.burgers.qminder.service.impl;

import com.qminder.burgers.qminder.dtos.burgerCheckerApi.BurgerDetectReqDto;
import com.qminder.burgers.qminder.dtos.burgerCheckerApi.BurgerDetectRespDto;
import com.qminder.burgers.qminder.dtos.fourSquare.photos.Item;
import com.qminder.burgers.qminder.dtos.fourSquare.photos.PhotosResponseDto;
import com.qminder.burgers.qminder.dtos.fourSquare.places.MainGeoCodeDto;
import com.qminder.burgers.qminder.dtos.fourSquare.places.PlaceRespDto;
import com.qminder.burgers.qminder.dtos.fourSquare.places.ResultRespDto;
import com.qminder.burgers.qminder.dtos.internal.BurgerPlaceDto;
import com.qminder.burgers.qminder.exception.BurgerErrorException;
import com.qminder.burgers.qminder.service.BurgerFinderService;
import com.qminder.burgers.qminder.utils.HeaderGeneraterUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BurgerFinderServiceImpl implements BurgerFinderService {
    private static final String BASE_API_URL = "https://api.foursquare.com/v3";
    private final WebClient webClient;
    private BiFunction<Integer, Integer, Integer> calculateNumberOfBatches =
            (totalNumberOfPicture, batchSize) ->
                    totalNumberOfPicture % batchSize > 0 ?
                            totalNumberOfPicture / batchSize + 1 :
                            totalNumberOfPicture / batchSize;

    @Value("${foursquare.token}")
    private String AUTH_TOKEN;
    @Value("${picture.pictureUrl}")
    private String DEFAULT_PICTURE_URL;
    @Value("${picture.burgerDetectorEndPoint}")
    private String BURGER_DETECTOR_END_POINT;
    @Value("${picture.oauth_token}")
    private String OAUTH_TOKEN_PICTURE;
    @Value("${foursquare.cookie}")
    private String BURGER_COOKIE_HEADERS;

    private Map<String, String> cookies;

    public BurgerFinderServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    @Cacheable(value = "getBurgerPlaces_cache")
    public List<BurgerPlaceDto> getBurgerPlaces() {
        log.info("Finding burger places!");
        List<PlaceRespDto> placeRespDtos = fetchBurgerPlaces();
        List<BurgerPlaceDto> places = placeRespDtos
                .stream()
                .map(this::prepareBurgerPlace)
                .collect(Collectors.toList());
        log.info("Found all burger places,count:{}", places.size());
        return places;
    }

    private BurgerPlaceDto prepareBurgerPlace(PlaceRespDto placeApiResponse) {
        MainGeoCodeDto mainGeocode = placeApiResponse.getGeocodes().getMainGeocode();

        Optional<Item> burgerItem = fetchPicture(placeApiResponse.getFsqID(), placeApiResponse.getName());
        String pictureUrl = burgerItem.isPresent() ? burgerItem.get().getPictureUrl() : DEFAULT_PICTURE_URL;
        return BurgerPlaceDto.builder()
                .name(placeApiResponse.getName())
                .latitude(mainGeocode.getLatitude().toString())
                .longitude(mainGeocode.getLongitude().toString())
                .location(placeApiResponse.getLocation().getFullAddress())
                .pictureUrl(pictureUrl)
                .build();
    }

    private Optional<Item> fetchPicture(String fsqID, String name) {
        int totalNumberOfPicture = fetchPhotoSize(fsqID, name);
        log.info("For picture id: {}, we need to fetch {} of pictures", fsqID, totalNumberOfPicture);
        int batchSize = 200; // site doesn't allow getting more
        int offset = 0;
        int limit = 0;
        int numberOfBatches = calculateNumberOfBatches.apply(totalNumberOfPicture, batchSize);

        List<Item> allItems = new ArrayList<>(totalNumberOfPicture);
        for (int i = 0; i < numberOfBatches; i++) {
            log.info("Fetching pictures for fsqId:{}.Batch:{}/{}", fsqID, i, numberOfBatches);
            offset = limit;
            limit = limit + batchSize;
            List<Item> items = scrapPictures(offset, limit, fsqID);
            allItems.addAll(items);
        }
        return findFirstBurger(allItems);
    }

    public Optional<Item> findFirstBurger(List<Item> allItems) {
        List<String> pictureLinks = allItems
                .stream()
                .parallel()
                .sorted()
                .map(Item::getPictureUrl)
                .collect(Collectors.toList());
        log.info("Sending all[{}] burger pictures to burger detector api.", allItems.size());
        BurgerDetectReqDto reqDto = BurgerDetectReqDto.builder().urls(pictureLinks).build();
        BurgerDetectRespDto burgerDetectRespDto = webClient
                .post()
                .uri(BURGER_DETECTOR_END_POINT)
                .header("Accept", "application/json")
                .body(Mono.just(reqDto), BurgerDetectReqDto.class)
                .retrieve()
                .onStatus(httpStatus -> !HttpStatus.OK.equals(httpStatus), clientResponse -> Mono.empty())//ignoreError
                .bodyToMono(BurgerDetectRespDto.class)
                .block();
        log.info("Burger api returned:{}", burgerDetectRespDto);
        return allItems.stream().parallel()
                .filter(item -> item.getPictureUrl().equals(burgerDetectRespDto.getUrlWithBurger()))
                .findFirst();
    }

    private List<Item> scrapPictures(int offset, int limit, String fsqID) {
        URI uri = buildScrapUri(offset, limit, fsqID);
        PhotosResponseDto photosResponseDto = webClient
                .get()
                .uri(uri)
                .header("Accept", "application/json")
                .retrieve()
                .onStatus(httpStatus -> !HttpStatus.OK.equals(httpStatus), clientResponse -> Mono.empty())//ignoreError
                .bodyToMono(PhotosResponseDto.class)
                .block();
        return Optional.ofNullable(Optional.ofNullable(photosResponseDto)
                        .orElseThrow(() -> new BurgerErrorException("No photos object found"))
                        .getResponseDto())
                .orElseThrow(() -> new BurgerErrorException("No response object found"))
                .extractPictureItems();
    }

    private URI buildScrapUri(int offset, int limit, String fsqID) {
        String searchPath = String.format("https://api.foursquare.com/v2/venues/%s/photos", fsqID);
        return UriComponentsBuilder.fromUriString(searchPath)
                .queryParam("locale", "en")
                .queryParam("v", "20211119")
                .queryParam("id", fsqID)
                .queryParam("offset", offset)
                .queryParam("limit", limit)
                .queryParam("oauth_token", OAUTH_TOKEN_PICTURE)
                .build().toUri();
    }

    public int fetchPhotoSize(String fsqId, String name) {
        //Generating part of path from name
        String namePath = name.toLowerCase(Locale.ROOT).replaceAll(" ", "-")
                .replaceAll("[^0-9a-zA-Z-]", "");
        String baseUrl = "https://foursquare.com/v/%s/%s/photos";
        String fullUrl = String.format(baseUrl, namePath, fsqId);
        try {
            String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36";
            //initially use default cookies to bypass automation scrapping check
            if (Objects.isNull(cookies)) {
                cookies = HeaderGeneraterUtil.generateHeaderMap(BURGER_COOKIE_HEADERS);
            }
            try {
                log.info("Sleep started");
                Thread.sleep(500);
                log.info("Sleep ended");
            } catch (InterruptedException e) {
                log.error("Sleep problem.");
            }
            Connection.Response response = Jsoup.connect(fullUrl)
                    .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .method(Connection.Method.GET)
                    .cookies(cookies)
                    .userAgent(USER_AGENT).execute();
            // then everytime update cookie with new result
            cookies = response.cookies();
            Document doc = response.parse();
            String fullSelectorToPhotoCount = "#container > div.venueDetail.hasPhoto > div.contents > div.venueInfoSection > div.sectionsBar > ul > li.photosBtn.sectionBtn.active > a > span.sectionCount";
            Elements photoCount = doc.select(fullSelectorToPhotoCount);
            if (photoCount.size() != 1) {
                log.error("FsqId: {}, Photo count elements expected: 1, got: {}", fsqId, photoCount.size());
                return 0; //No photo exists.
            } else {
                Element count = photoCount.get(0);
                return Integer.parseInt(count.text().replace(",", ""));
            }
        } catch (IOException e) {
            throw new BurgerErrorException("JSoup error:", e);
        }
    }

    private List<PlaceRespDto> fetchBurgerPlaces() {
        log.info("Fetching burgers from external api.");
        URI uri = buildSearchQuery();
        ResultRespDto resultRespDto = webClient
                .get()
                .uri(uri)
                .header("Accept", "application/json")
                .header("Authorization", AUTH_TOKEN)
                .retrieve()
                .onStatus(httpStatus -> !HttpStatus.OK.equals(httpStatus), clientResponse -> Mono.empty())//ignoreError
                .bodyToMono(ResultRespDto.class)
                .block();
        if (Objects.isNull(resultRespDto)) {
            log.info("No burger places found");
            return Collections.emptyList();
        }
        log.info("{} burgers fetched.", resultRespDto.getPlaces().size());
        return resultRespDto.getPlaces();
    }

    private URI buildSearchQuery() {
        String searchPath = String.format("%s/places/search", BASE_API_URL);
        //?query=Burger%20Joint&near=Tartu&sort=DISTANCE&limit=50
        return UriComponentsBuilder.fromUriString(searchPath)
                .queryParam("query", "Burger Joint")
                .queryParam("near", "Tartu")
                .queryParam("sort", "DISTANCE")
                .queryParam("limit", 50)
                .build().toUri();
    }
}
