package com.qminder.burgers.qminder.service;

import com.qminder.burgers.qminder.dtos.internal.BurgerPlaceDto;

import java.util.List;

public interface BurgerFinderService {
    List<BurgerPlaceDto> getBurgerPlaces();
}
