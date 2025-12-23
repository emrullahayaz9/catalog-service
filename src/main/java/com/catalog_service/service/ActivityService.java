package com.catalog_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.catalog_service.dto.InteractionRequest;
import com.catalog_service.dto.WatchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ActivityService
 * Functions as a Kafka Producer within the Catalog & Activity Service.
 * Publishes user behavior events to facilitate asynchronous communication with the AI Engine.
 */
@Service
@RequiredArgsConstructor
public class ActivityService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Kafka Topics defined in the messaging layer
    private static final String TOPIC_INTERACTION = "interaction-events";
    private static final String TOPIC_WATCH = "watch-events";

    /**
     * Processes implicit feedback (watch logs) and broadcasts to the watch-events topic.
     * Calculates completion percentage to assist the Recommendation Engine's weighting logic.
     */
    public void logWatch(WatchRequest req, int totalMovieDuration) {
        try {
            // Logic to determine if a movie is considered 'completed' for analytical purposes
            double progress = (double) req.getCurrentPosition() / totalMovieDuration;
            boolean isCompleted = progress > 0.90;

            // Mapping raw request data into a standardized event payload
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("userId", req.getUserId());
            eventData.put("movieId", req.getMovieId());
            eventData.put("currentPosition", req.getCurrentPosition());
            eventData.put("watchedDuration", req.getWatchedDuration());
            eventData.put("deviceType", req.getDeviceType());
            eventData.put("completionPercentage", progress);
            eventData.put("isCompleted", isCompleted);
            eventData.put("timestamp", LocalDateTime.now().toString());

            // Serialize payload to JSON string for Kafka transmission
            String json = objectMapper.writeValueAsString(eventData);
            System.out.println("watch update request: " + json);

            // Send message to Kafka using UserId as the partitioning key for order guarantee
            kafkaTemplate.send(TOPIC_WATCH, req.getUserId().toString(), json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Dispatches explicit feedback (ratings/likes) to the interaction-events topic.
     */
    public void logInteraction(InteractionRequest req) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("userId", req.getUserId());
            eventData.put("movieId", req.getMovieId());
            eventData.put("rating", req.getRating());
            eventData.put("likeStatus", req.getLikeStatus());
            eventData.put("inMyList", req.getAddToList());
            eventData.put("timestamp", LocalDateTime.now().toString());

            String json = objectMapper.writeValueAsString(eventData);
            System.out.println("interaction request: " + json);

            // Publishing the explicit feedback event asynchronously
            kafkaTemplate.send(TOPIC_INTERACTION, req.getUserId().toString(), json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}