# About the project
- Scraps burger places in Tartu by fetching information from Foursquare API
- Detects burger pictures calling provided detector API
- Returns fetched data in REST API and visual format
- First request takes 30-40 seconds, hence, the rest of them will be directly answered. I use caching mechanism to decrease response time. Cache is released in 4 hours.
- Deployed to Heroku

# Used technology
- Spring boot
- Spring MVC
- Caffeine Cache
- Thymeleaf
- Spring Web Client
# How to check?
- directly on [Heroku](http://tartu-burgers.herokuapp.com/)
- REST API [end point](http://tartu-burgers.herokuapp.com/burger)
- locally: By default it runs on port 5000. You need to provide [FOURSQUARE_TOKEN](https://developer.foursquare.com/docs/places-api-getting-started), [OAUTH_TOKEN_PICTURE](#How-to-parse-picture), `BURGER_DETECTOR_END_POINT`as environment variables.
- 
# How I did?
 1. GET `https://api.foursquare.com/v3/places/search`

*Request*
  ```java 
OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
.url("https://api.foursquare.com/v3/places/search?query=Burger%20Joint&near=Tartu&sort=DISTANCE&limit=50")
.get()
.addHeader("Accept", "application/json")
.addHeader("Authorization", "YOUR_KEY")
.build();

Response response = client.newCall(request).execute(); 
```
 *Response* 
[here](/examples/placesTartu.json)



# How to parse picture
1. https://foursquare.com/v/{FSQ_ID}/photos open this link.
2. In browser, open `developer tools`, go to `network tab`
3. Click on `See more photos` in web-site
4. In `network` select `Fetch/XHR`
5. Search for `photos?`
6. That link is the source of the pictures.
7. `oauth_token` should be set to `OAUTH_TOKEN_PICTURE` in application properties
8. `offset` and `limit`. Increase offset and limit 200 by 200 to fetch pictures. It is max limit.
`https://api.foursquare.com/v2/venues/4c222be413c00f47040587de/photos?locale=en&explicit-lang=false&v=20211119&id=49e9ef74f964a52011661fe3&offset=0&limit=60&wsid=MITWDHNTSPRFFKMGYOCGFDDU4GELG0&oauth_token=QEJ4AQPTMMNB413HGNZ5YDMJSHTOHZHMLZCAQCCLXIX41OMP`