package com.example.moviewatchlog.integration.impl;

import com.example.moviewatchlog.integration.StreamingAvailabilityClient;
import com.example.moviewatchlog.integration.tmdb.TmdbSearchResponse;
import com.example.moviewatchlog.integration.tmdb.TmdbWatchProvidersResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TmdbStreamingAvailabilityClient implements StreamingAvailabilityClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;
    private final String country;

    public TmdbStreamingAvailabilityClient(RestTemplate restTemplate,
                                           @Value("${tmdb.api.base-url}") String baseUrl,
                                           @Value("${tmdb.api.key}") String apiKey,
                                           @Value("${tmdb.api.country:US}") String country) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.country = country;
    }

    @Override
    public List<String> getStreamingPlatforms(String title, Integer year) {
        try {
            Integer movieId = findMovieId(title, year);
            if (movieId == null) {
                return Collections.emptyList();
            }
            return getProvidersForMovie(movieId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private Integer findMovieId(String title, Integer year) {
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        StringBuilder url = new StringBuilder(
                String.format("%s/search/movie?api_key=%s&query=%s",
                        baseUrl, apiKey, encodedTitle)
        );
        if (year != null) {
            url.append("&year=").append(year);
        }
        TmdbSearchResponse response = restTemplate.getForObject(url.toString(), TmdbSearchResponse.class);
        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            return null;
        }
        return response.getResults().get(0).getId();
    }

    private List<String> getProvidersForMovie(Integer movieId) {
        String url = String.format("%s/movie/%d/watch/providers?api_key=%s",
                baseUrl, movieId, apiKey);
        TmdbWatchProvidersResponse response = restTemplate.getForObject(url, TmdbWatchProvidersResponse.class);
        if (response == null || response.getResults() == null) {
            return Collections.emptyList();
        }
        TmdbWatchProvidersResponse.CountryProviders countryProviders = response.getResults().get(country);
        if (countryProviders == null) {
            return Collections.emptyList();
        }
        Stream<TmdbWatchProvidersResponse.Provider> stream = Stream.empty();
        if (countryProviders.getFlatrate() != null) {
            stream = Stream.concat(stream, countryProviders.getFlatrate().stream());
        }
        if (countryProviders.getRent() != null) {
            stream = Stream.concat(stream, countryProviders.getRent().stream());
        }
        if (countryProviders.getBuy() != null) {
            stream = Stream.concat(stream, countryProviders.getBuy().stream());
        }
        return stream
                .map(TmdbWatchProvidersResponse.Provider::getProvider_name)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
