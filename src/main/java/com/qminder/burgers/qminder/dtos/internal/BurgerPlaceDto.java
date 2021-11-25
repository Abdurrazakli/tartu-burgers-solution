package com.qminder.burgers.qminder.dtos.internal;

import lombok.Data;

import java.io.Serializable;

@Data
public class BurgerPlaceDto implements Serializable {
    private String location;
    private String latitude;
    private String longitude;
    private String pictureUrl;
}
