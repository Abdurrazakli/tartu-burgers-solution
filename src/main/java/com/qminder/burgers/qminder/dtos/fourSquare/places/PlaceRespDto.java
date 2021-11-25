package com.qminder.burgers.qminder.dtos.fourSquare.places;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PlaceRespDto implements Serializable {
    @JsonProperty("fsq_id")
    private String fsqID;
    @JsonProperty("name")
    private String name = "";
    @JsonProperty("geocodes")
    private GeoCodeDto geocodes;
    @JsonProperty("location")
    private LocationDto location;
}