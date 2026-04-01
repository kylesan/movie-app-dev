package com.example.moviewatchlog.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "movies")
public class Movie {

    @Id
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
