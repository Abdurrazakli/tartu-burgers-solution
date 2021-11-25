package com.qminder.burgers.qminder.dtos.fourSquare.places;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GeoCodeDto implements Serializable {
    @JsonProperty("main")
    private MainGeoCodeDto mainGeocode;
}
