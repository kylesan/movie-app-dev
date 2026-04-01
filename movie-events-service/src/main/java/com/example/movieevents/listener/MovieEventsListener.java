package com.example.movieevents.listener;

import com.example.movieevents.search.MovieSearchDocument;
import com.example.movieevents.search.MovieSearchRepository;
import com.example.moviewatchlog.events.MovieCreatedEvent;
import com.example.moviewatchlog.events.MovieDeletedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MovieEventsListener {

    private final MovieSearchRepository repository;

    public MovieEventsListener(MovieSearchRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "movie-events", groupId = "movie-events-service")
    public void handleMovieCreated(MovieCreatedEvent event) {
        MovieSearchDocument doc = new MovieSearchDocument();
        doc.setId(event.getMovieId());
        doc.setTitle(event.getTitle());
        doc.setTitle(event.getTitle());
        doc.setWatchedYear(event.getWatchedYear());
        doc.setWatchedAt(event.getWatchedAt());
        doc.setCoverUrl(event.getCoverUrl());
        doc.setSource(event.getSource());
        doc.setSourceUser(event.getSourceUser());
        doc.setOwnerId(event.getOwnerId());
        doc.setStreamingPlatforms(event.getStreamingPlatforms());
        repository.save(doc);
    }

    @KafkaListener(topics = "movie-deleted-events", groupId = "movie-events-service")
    public void handleMovieDeleted(MovieDeletedEvent event) {
        if (event.getId() != null) {
            repository.deleteById(event.getId());
        }
    }
}