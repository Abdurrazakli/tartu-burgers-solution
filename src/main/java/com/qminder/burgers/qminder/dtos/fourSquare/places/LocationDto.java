package com.qminder.burgers.qminder.dtos.fourSquare.places;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;

import java.io.Serializable;

@Data
public class LocationDto implements Serializable {
    @JsonProperty("address")
    private String address = "";
    @JsonProperty("cross_street")
    private String crossStreet = "";
}
