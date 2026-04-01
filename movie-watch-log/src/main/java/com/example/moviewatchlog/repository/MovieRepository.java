package com.example.moviewatchlog.repository;

import com.example.moviewatchlog.model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends MongoRepository<Movie, String> {

    List<Movie> findByOwnerIdAndWatchedYearOrderByWatchedAtAsc(String ownerId, Integer watchedYear);

    Optional<Movie> findByIdAndOwnerId(String id, String ownerId);

    // ✅ NEW: look up by movieId (plus owner for safety)
    Optional<Movie> findByMovieIdAndOwnerId(String movieId, String ownerId);

    // ✅ NEW: delete by movieId (plus owner)
    void deleteByMovieIdAndOwnerId(String movieId, String ownerId);
}
