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

@Service
@RequiredArgsConstructor
public class DataPersistConsumer {

    private final InteractionRepository interactionRepo;
    private final WatchSessionRepository sessionRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "watch-events", groupId = "db-writer-group")
    @Transactional
    public void consumeWatchEvent(String message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});

            Long userId = ((Number) data.get("userId")).longValue();
            String movieId = (String) data.get("movieId");

            WatchSession session = sessionRepo.findByUserIdAndMovieId(userId, movieId)
                    .orElse(new WatchSession());

            session.setUserId(userId);
            session.setMovieId(movieId);
            session.setCurrentPositionSeconds((Integer) data.get("currentPosition"));
            session.setWatchedDurationSeconds(session.getWatchedDurationSeconds() + (Integer) data.get("watchedDuration"));
            session.setCompletionPercentage((Double) data.get("completionPercentage"));
            session.setCompleted((Boolean) data.get("isCompleted"));
            session.setDeviceType((String) data.get("deviceType"));
            session.setLastWatchedAt(LocalDateTime.parse((String) data.get("timestamp")));

            sessionRepo.save(session);


        } catch (Exception e) {
            System.err.println("Hata oluştu (Watch): " + e.getMessage());
        }
    }

    @KafkaListener(topics = "interaction-events", groupId = "db-writer-group")
    @Transactional
    public void consumeInteractionEvent(String message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});

            Long userId = ((Number) data.get("userId")).longValue();
            String movieId = (String) data.get("movieId");

            UserInteraction interaction = interactionRepo.findByUserIdAndMovieId(userId, movieId)
                    .orElse(new UserInteraction());

            interaction.setUserId(userId);
            interaction.setMovieId(movieId);
            interaction.setInteractionDate(LocalDateTime.parse((String) data.get("timestamp")));

            if (data.get("rating") != null) interaction.setRating(((Number) data.get("rating")).doubleValue());
            if (data.get("likeStatus") != null) interaction.setLikeStatus((Integer) data.get("likeStatus"));
            if (data.get("inMyList") != null) interaction.setInMyList((Boolean) data.get("inMyList"));

            interactionRepo.save(interaction);

        } catch (Exception e) {
            System.err.println("Hata oluştu (Interaction): " + e.getMessage());
        }
    }
}
