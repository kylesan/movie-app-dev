package com.example.moviewatchlog.integration.tmdb;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TmdbWatchProvidersResponse {

    private Map<String, CountryProviders> results;

    @Data
    public static class CountryProviders {
        private List<Provider> flatrate;
        private List<Provider> rent;
        private List<Provider> buy;
    }

    @Data
    public static class Provider {
        private Integer provider_id;
        private String provider_name;
        private String logo_path;
    }
}
