package com.catalog_service.service;

import com.catalog_service.entity.Movie;
import com.catalog_service.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final MovieRepository repository;

    @Transactional
    public Movie addMovie(Movie movie) {

        if (movie.getCreatedDate() == null) {
            movie.setCreatedDate(LocalDate.now());
        }

        Movie saved = repository.save(movie);

        System.out.println("✅ Film eklendi: " + saved.getTitle());

        return saved;
    }
}