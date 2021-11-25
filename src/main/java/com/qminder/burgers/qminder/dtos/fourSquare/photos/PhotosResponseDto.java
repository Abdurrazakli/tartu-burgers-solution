package com.qminder.burgers.qminder.dtos.fourSquare.photos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PhotosResponseDto implements Serializable {
    @JsonProperty("response")
    private ResponseDto responseDto;
}
