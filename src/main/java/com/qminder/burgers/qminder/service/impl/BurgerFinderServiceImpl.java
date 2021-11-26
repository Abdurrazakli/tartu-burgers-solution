package com.qminder.burgers.qminder.service.impl;

import com.qminder.burgers.qminder.dtos.fourSquare.places.PlaceRespDto;
import com.qminder.burgers.qminder.dtos.fourSquare.places.ResultRespDto;
import com.qminder.burgers.qminder.dtos.internal.BurgerPlaceDto;
import com.qminder.burgers.qminder.service.BurgerFinderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class BurgerFinderServiceImpl implements BurgerFinderService {
    private static final String BASE_API_URL = "https://api.foursquare.com/v3";
    private final WebClient webClient;

    @Value("${foursquare.token}")
    private String AUTH_TOKEN;

    public BurgerFinderServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<BurgerPlaceDto> getBurgerPlaces() {
        List<PlaceRespDto> placeRespDtos = fetchBurgerPlaces();
        return Collections.emptyList();
//        throw new IllegalArgumentException("Implement me");
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
                .bodyToMono(ResultRespDto.class)
                .block();
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
