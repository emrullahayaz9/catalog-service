package com.catalog_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// watch_sessions table
@Entity
@Data
@Table(name = "watch_sessions", indexes = {
        @Index(name = "idx_user_movie", columnList = "userId, movieId")
})
public class WatchSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String movieId;

    private int currentPositionSeconds;

    private int watchedDurationSeconds;

    private double completionPercentage;

    private boolean isCompleted;

    private String deviceType;

    private LocalDateTime lastWatchedAt;

    private LocalDateTime startedAt;

    private int sessionCount;
}