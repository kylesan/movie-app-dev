package com.example.moviewatchlog.events;

import lombok.Data;

import java.util.List;

@Data
public class MovieCreatedEvent {
    private String id;
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
