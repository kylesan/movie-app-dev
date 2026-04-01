package com.example.movieevents.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.Instant;

@Data
@Document(indexName = "movie-events")
public class MovieEventDocument {

    @Id
    private String id;

    private String movieId;
    private String title;
    private Integer watchedYear;
    private Instant watchedAt;

    public MovieEventDocument() {}

    public MovieEventDocument(String id, String movieId, String title, Integer watchedYear, Instant watchedAt) {
        this.id = id;
        this.movieId = movieId;
        this.title = title;
        this.watchedYear = watchedYear;
        this.watchedAt = watchedAt;
    }
}