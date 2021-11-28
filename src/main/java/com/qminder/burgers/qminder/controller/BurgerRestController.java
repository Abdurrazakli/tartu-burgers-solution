package com.qminder.burgers.qminder.controller;

import com.qminder.burgers.qminder.dtos.internal.BurgerPlaceDto;
import com.qminder.burgers.qminder.service.BurgerFinderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/burger")
public class BurgerRestController {
    private final BurgerFinderService burgerFinderService;

    public BurgerRestController(BurgerFinderService burgerFinderService) {
        this.burgerFinderService = burgerFinderService;
    }

    @GetMapping
    public ResponseEntity<List<BurgerPlaceDto>> getBurgers() {
        return ResponseEntity.ok(burgerFinderService.getBurgerPlaces());
    }
}
