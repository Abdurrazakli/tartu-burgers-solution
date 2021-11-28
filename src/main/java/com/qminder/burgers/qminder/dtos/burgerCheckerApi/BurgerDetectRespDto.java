package com.qminder.burgers.qminder.dtos.burgerCheckerApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BurgerDetectRespDto implements Serializable {
    @JsonProperty("urlWithBurger")
    private String urlWithBurger;
}
