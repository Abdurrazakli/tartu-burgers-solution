package com.qminder.burgers.qminder.dtos.fourSquare.places;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ResultRespDto implements Serializable {
    @JsonProperty("results")
    private List<PlaceRespDto> places;

}
