package com.catalog_service.dto;

import lombok.Data;

// interaction dto
@Data
public class InteractionRequest {
    private Long userId;
    private String movieId;
    private Double rating;      // 1-5
    private Integer likeStatus; // 1: Like, -1: Dislike
    private Boolean addToList;
}
