package com.catalog_service.controller;

import com.catalog_service.dto.InteractionRequest;
import com.catalog_service.dto.WatchRequest;
import com.catalog_service.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService service;

    @PostMapping("/watch")
    public void logWatch(@RequestBody WatchRequest request) {
        int totalDuration = 7200;
        service.logWatch(request, totalDuration);
    }

    @PostMapping("/interact")
    public void logInteraction(@RequestBody InteractionRequest request) {
        service.logInteraction(request);
    }
}
