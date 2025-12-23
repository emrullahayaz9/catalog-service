package com.catalog_service.controller;

import com.catalog_service.dto.InteractionRequest;
import com.catalog_service.dto.WatchRequest;
import com.catalog_service.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * ActivityController
 * Serves as the ingestion point for user behavior data in the Catalog Service.
 * Triggers asynchronous event processing through Kafka.
 */
@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService service;

    /**
     * Captures implicit feedback (watch duration logs) from the Frontend.
     * Forwards data to the ActivityService to be published as a Kafka event.
     */
    @PostMapping("/watch")
    public void logWatch(@RequestBody WatchRequest request) {
        // Mocking total duration for completion percentage calculation logic
        int totalDuration = 7200;
        service.logWatch(request, totalDuration);
    }

    /**
     * Captures explicit feedback (Likes, Super Likes) from the Frontend.
     * Updates the user profile in the analytics engine via asynchronous messaging.
     */
    @PostMapping("/interact")
    public void logInteraction(@RequestBody InteractionRequest request) {
        service.logInteraction(request);
    }
}