package com.qminder.burgers.qminder.dtos.burgerCheckerApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class BurgerDetectReqDto implements Serializable {
    @JsonProperty("urls")
    private List<String> urls;
}
