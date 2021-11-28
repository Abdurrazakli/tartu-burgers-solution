package com.qminder.burgers.qminder.dtos.internal;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class BurgerPlaceDto implements Serializable {
    private String name;
    private String location;
    private String latitude;
    private String longitude;
    private String pictureUrl;
}
