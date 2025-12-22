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

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TOPIC_INTERACTION = "interaction-events";
    private static final String TOPIC_WATCH = "watch-events";

    public void logWatch(WatchRequest req, int totalMovieDuration) {
        try {
            double progress = (double) req.getCurrentPosition() / totalMovieDuration;
            boolean isCompleted = progress > 0.90;

            Map<String, Object> eventData = new HashMap<>();
            eventData.put("userId", req.getUserId());
            eventData.put("movieId", req.getMovieId());
            eventData.put("currentPosition", req.getCurrentPosition());
            eventData.put("watchedDuration", req.getWatchedDuration());
            eventData.put("deviceType", req.getDeviceType());
            eventData.put("completionPercentage", progress);
            eventData.put("isCompleted", isCompleted);
            eventData.put("timestamp", LocalDateTime.now().toString());

            String json = objectMapper.writeValueAsString(eventData);
            System.out.println("watch update request: " + json);

            kafkaTemplate.send(TOPIC_WATCH, req.getUserId().toString(), json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            kafkaTemplate.send(TOPIC_INTERACTION, req.getUserId().toString(), json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}