package com.catalog_service.dto;

import lombok.Data;

// WatchRequest dto
@Data
public class WatchRequest {
    private Long userId;
    private String movieId;
    private int currentPosition;
    private int watchedDuration;
    private String deviceType;   // TV, MOBILE
}


