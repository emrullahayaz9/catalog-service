package com.catalog_service.repository;


import com.catalog_service.entity.WatchSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WatchSessionRepository extends JpaRepository<WatchSession, Long> {

    Optional<WatchSession> findByUserIdAndMovieId(Long userId, String movieId);
}
