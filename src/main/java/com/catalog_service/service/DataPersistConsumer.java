package com.catalog_service.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.catalog_service.entity.UserInteraction;
import com.catalog_service.entity.WatchSession;
import com.catalog_service.repository.InteractionRepository;
import com.catalog_service.repository.WatchSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DataPersistConsumer
 * Acts as a Kafka Consumer within the Catalog Service to handle data persistence.
 * Listens to activity events and updates the Catalog DB for record-keeping and synchronization.
 */
@Service
@RequiredArgsConstructor
public class DataPersistConsumer {

    private final InteractionRepository interactionRepo;
    private final WatchSessionRepository sessionRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Consumes watch progress events from the "watch-events" topic.
     * Upserts (Updates or Inserts) the watch session record to maintain an accurate history of user viewing progress.
     */
    @KafkaListener(topics = "watch-events", groupId = "db-writer-group")
    @Transactional
    public void consumeWatchEvent(String message) {
        try {
            // Deserializing the JSON event payload received from the Kafka broker
            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});

            Long userId = ((Number) data.get("userId")).longValue();
            String movieId = (String) data.get("movieId");

            // Retrieve existing session or initialize a new one for the specific user and movie
            WatchSession session = sessionRepo.findByUserIdAndMovieId(userId, movieId)
                    .orElse(new WatchSession());

            // Updating session metadata with the latest event data
            session.setUserId(userId);
            session.setMovieId(movieId);
            session.setCurrentPositionSeconds((Integer) data.get("currentPosition"));
            session.setWatchedDurationSeconds(session.getWatchedDurationSeconds() + (Integer) data.get("watchedDuration"));
            session.setCompletionPercentage((Double) data.get("completionPercentage"));
            session.setCompleted((Boolean) data.get("isCompleted"));
            session.setDeviceType((String) data.get("deviceType"));
            session.setLastWatchedAt(LocalDateTime.parse((String) data.get("timestamp")));

            // Persisting the updated session to the Catalog Database
            sessionRepo.save(session);


        } catch (Exception e) {
            System.err.println("Error occurred during Watch event consumption: " + e.getMessage());
        }
    }

    /**
     * Consumes explicit feedback events from the "interaction-events" topic.
     * Records likes, ratings, and list status to the persistent database.
     */
    @KafkaListener(topics = "interaction-events", groupId = "db-writer-group")
    @Transactional
    public void consumeInteractionEvent(String message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});

            Long userId = ((Number) data.get("userId")).longValue();
            String movieId = (String) data.get("movieId");

            // Upsert logic for user interactions (likes/ratings)
            UserInteraction interaction = interactionRepo.findByUserIdAndMovieId(userId, movieId)
                    .orElse(new UserInteraction());

            interaction.setUserId(userId);
            interaction.setMovieId(movieId);
            interaction.setInteractionDate(LocalDateTime.parse((String) data.get("timestamp")));

            // Conditional updates based on provided payload fields (Explicit Feedback)
            if (data.get("rating") != null) interaction.setRating(((Number) data.get("rating")).doubleValue());
            if (data.get("likeStatus") != null) interaction.setLikeStatus((Integer) data.get("likeStatus"));
            if (data.get("inMyList") != null) interaction.setInMyList((Boolean) data.get("inMyList"));

            interactionRepo.save(interaction);

        } catch (Exception e) {
            System.err.println("Error occurred during Interaction event consumption: " + e.getMessage());
        }
    }
}