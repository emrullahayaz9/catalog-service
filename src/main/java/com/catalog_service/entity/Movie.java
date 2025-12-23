package com.catalog_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

// Movie table
@Entity
@Data
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    @Column(length = 2000)
    private String description;

    private Integer releaseYear;
    private Integer durationMinutes;
    private Double globalImdbRating;
    private String maturityRating;

    @ElementCollection
    private List<String> genres;

    @ElementCollection
    @Column(name = "cast_member")
    private List<String> cast;

    private String director;

    @ElementCollection
    private List<String> tags;

    @ElementCollection
    private List<String> availableLanguages;

    private LocalDate createdDate;
}