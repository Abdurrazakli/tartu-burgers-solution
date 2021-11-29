package com.qminder.burgers.qminder.controller;

import com.qminder.burgers.qminder.dtos.internal.BurgerPlaceDto;
import com.qminder.burgers.qminder.service.BurgerFinderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/")
public class BurgerController {
    private final BurgerFinderService burgerFinderService;

    public BurgerController(BurgerFinderService burgerFinderService) {
        this.burgerFinderService = burgerFinderService;
    }

    @GetMapping
    public String getIndexPage(Model model) {
        log.info("Index page is loading");
        List<BurgerPlaceDto> burgerPlaces = burgerFinderService.getBurgerPlaces();
        model.addAttribute("dataList", burgerPlaces);
        log.info("Index page loaded");
        return "index";
    }
}
