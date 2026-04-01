package com.example.moviewatchlog.service;

import com.example.moviewatchlog.events.MovieCreatedEvent;
import com.example.moviewatchlog.events.MovieDeletedEvent;
import com.example.moviewatchlog.exception.MovieNotFoundException;
import com.example.moviewatchlog.integration.OmdbClient;
import com.example.moviewatchlog.integration.StreamingAvailabilityClient;
import com.example.moviewatchlog.model.Movie;
import com.example.moviewatchlog.repository.MovieRepository;
import com.example.moviewatchlog.security.UserUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final OmdbClient omdbClient;
    private final StreamingAvailabilityClient streamingAvailabilityClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String movieEventsTopic;
    private final String movieDeletedTopic;

    public MovieService(MovieRepository movieRepository,
                        OmdbClient omdbClient,
                        StreamingAvailabilityClient streamingAvailabilityClient,
                        KafkaTemplate<String, Object> kafkaTemplate,
                        @Value("${movie.events.topic:movie-events}") String movieEventsTopic,
                        @Value("${movie.events.deleted-topic:movie-deleted-events}") String movieDeletedTopic) {
        this.movieRepository = movieRepository;
        this.omdbClient = omdbClient;
        this.streamingAvailabilityClient = streamingAvailabilityClient;
        this.kafkaTemplate = kafkaTemplate;
        this.movieEventsTopic = movieEventsTopic;
        this.movieDeletedTopic = movieDeletedTopic;
    }

    public Movie createMovie(Movie movie) {
        String ownerId = UserUtils.getCurrentUserId();
        movie.setOwnerId(ownerId);

        if (movie.getWatchedAt() == null) {
            movie.setWatchedAt(OffsetDateTime.now(ZoneOffset.UTC).toString());
        }
        if (movie.getWatchedYear() == null) {
            movie.setWatchedYear(OffsetDateTime.parse(movie.getWatchedAt()).getYear());
        }

        if (movie.getCoverUrl() == null || movie.getCoverUrl().isBlank()) {
            String cover = omdbClient.fetchPoster(movie.getTitle(), movie.getWatchedYear());
            movie.setCoverUrl(cover);
        }

        if (movie.getStreamingPlatforms() == null || movie.getStreamingPlatforms().isEmpty()) {
            var platforms = streamingAvailabilityClient.getStreamingPlatforms(
                    movie.getTitle(), movie.getWatchedYear());
            movie.setStreamingPlatforms(platforms);
        }

        Movie saved = movieRepository.save(movie);

        // publish MovieCreatedEvent
        MovieCreatedEvent event = new MovieCreatedEvent();
        event.setId(saved.getId());
        event.setMovieId(saved.getMovieId());
        event.setTitle(saved.getTitle());
        event.setWatchedYear(saved.getWatchedYear());
        event.setWatchedAt(saved.getWatchedAt());
        event.setCoverUrl(saved.getCoverUrl());
        event.setSource(saved.getSource());
        event.setSourceUser(saved.getSourceUser());
        event.setOwnerId(saved.getOwnerId());
        event.setStreamingPlatforms(saved.getStreamingPlatforms());

        kafkaTemplate.send(movieEventsTopic, saved.getId(), event);

        return saved;
    }

    public List<Movie> getMoviesByYear(Integer year) {
        String ownerId = UserUtils.getCurrentUserId();
        return movieRepository.findByOwnerIdAndWatchedYearOrderByWatchedAtAsc(ownerId, year);
    }

    public Movie getMovie(String id) {
        String ownerId = UserUtils.getCurrentUserId();
        boolean admin = UserUtils.isAdmin();
        if (admin) {
            return movieRepository.findById(id)
                    .orElseThrow(() -> new MovieNotFoundException(id));
        }
        return movieRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    public Movie updateMovie(String id, Movie updated) {
        String ownerId = UserUtils.getCurrentUserId();
        boolean admin = UserUtils.isAdmin();

        Movie existing = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        if (!admin && (existing.getOwnerId() == null ||
                !existing.getOwnerId().equals(ownerId))) {
            throw new MovieNotFoundException(id);
        }

        existing.setTitle(updated.getTitle());
        existing.setWatchedYear(updated.getWatchedYear());
        existing.setWatchedAt(updated.getWatchedAt());
        existing.setCoverUrl(updated.getCoverUrl());
        existing.setSource(updated.getSource());
        existing.setSourceUser(updated.getSourceUser());
        existing.setStreamingPlatforms(updated.getStreamingPlatforms());

        Movie saved = movieRepository.save(existing);

        // reuse MovieCreatedEvent as an "upsert" event
        MovieCreatedEvent event = new MovieCreatedEvent();
        event.setId(saved.getId());
        event.setTitle(saved.getTitle());
        event.setWatchedYear(saved.getWatchedYear());
        event.setWatchedAt(saved.getWatchedAt());
        event.setCoverUrl(saved.getCoverUrl());
        event.setSource(saved.getSource());
        event.setSourceUser(saved.getSourceUser());
        event.setOwnerId(saved.getOwnerId());
        event.setStreamingPlatforms(saved.getStreamingPlatforms());

        kafkaTemplate.send(movieEventsTopic, saved.getId(), event);

        return saved;
    }

    public void deleteMovie(String id) {
        String ownerId = UserUtils.getCurrentUserId();
        boolean admin = UserUtils.isAdmin();

        Movie existing = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        if (!admin && (existing.getOwnerId() == null ||
                !existing.getOwnerId().equals(ownerId))) {
            throw new MovieNotFoundException(id);
        }

        movieRepository.deleteById(id);

        MovieDeletedEvent event = new MovieDeletedEvent();
        event.setMovieId(id);

        kafkaTemplate.send(movieDeletedTopic, id, event);
    }

}