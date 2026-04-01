package com.example.movieevents.search;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Data
@Document(indexName = "movies-events")
public class MovieSearchDocument {

    @Id
    private String id;

    // ✅ New: actual Mongo movie id from movie-watch-log
    private String movieId;

    private String title;
    private Integer watchedYear;
    private String watchedAt;
    private String coverUrl;
    private String source;
    private String sourceUser;
    private String ownerId;
    private List<String> streamingPlatforms;

}