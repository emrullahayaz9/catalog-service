package com.catalog_service.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MovieMetadataEvent {
    private String movieId;
    private String title;
    private String description;
    private List<String> genres;
    private List<String> cast;
    private List<String> tags;
    private String director;
    private Double imdbRating;
    private String maturityRating;
    private String eventType; // "CREATED", "UPDATED"
}
