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
- increase offset 200 by 200
`https://api.foursquare.com/v2/venues/49e9ef74f964a52011661fe3/photos?locale=en&explicit-lang=false&v=20211119&id=49e9ef74f964a52011661fe3&offset=600&limit=6000&wsid=TVITXVUC2EDV2W2SJ3SMDDY0ZZE067&oauth_token=TTRWRZN3UTS5M2HI10NNFFV4Q2BEM4QF3IPBIYBDEPRJIPY2`
  