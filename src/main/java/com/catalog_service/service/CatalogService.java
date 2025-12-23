package com.catalog_service.service;

import com.catalog_service.entity.Movie;
import com.catalog_service.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * CatalogService
 * Manages the persistent storage and retrieval of movie metadata.
 * Serves as the primary data provider for the Movie Catalog UI.
 */
@Service
@RequiredArgsConstructor
public class CatalogService {

    private final MovieRepository repository;

    /**
     * Persists a new Movie entity into the Catalog Database.
     * Ensures all entries have a valid creation date for chronological sorting.
     */
    @Transactional
    @SuppressWarnings("unused")
    public Movie addMovie(Movie movie) {

        // Auto-sets the creation date if not provided by the requester
        if (movie.getCreatedDate() == null) {
            movie.setCreatedDate(LocalDate.now());
        }

        // Saves the entity using Spring Data JPA
        Movie saved = repository.save(movie);

        return saved;
    }
}