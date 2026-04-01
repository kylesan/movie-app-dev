package com.example.moviewatchlog.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class MovieResponse {

    private String id;
    private String title;
    private Integer watchedYear;
    private String watchedAt;
    private String coverUrl;
    private String source;
    private String sourceUser;
    private List<String> streamingPlatforms;
}
