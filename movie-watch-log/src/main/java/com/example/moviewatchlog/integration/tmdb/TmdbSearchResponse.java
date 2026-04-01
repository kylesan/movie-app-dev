package com.example.moviewatchlog.integration.tmdb;

import lombok.Data;

import java.util.List;

@Data
public class TmdbSearchResponse {
    private List<TmdbMovieResult> results;

    @Data
    public static class TmdbMovieResult {
        private Integer id;
        private String title;
        private String release_date;
    }
}
