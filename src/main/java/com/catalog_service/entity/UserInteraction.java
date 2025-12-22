package com.catalog_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_interactions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "movieId"})
})
public class UserInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String movieId;

    private Double rating;

    private Integer likeStatus;

    private boolean isInMyList;

    private LocalDateTime interactionDate;
}
