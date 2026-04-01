package com.example.moviewatchlog.integration.impl;

import com.example.moviewatchlog.integration.OmdbClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class OmdbClientImpl implements OmdbClient {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;

    public OmdbClientImpl(RestTemplate restTemplate,
                          @Value("${omdb.api.key}") String apiKey,
                          @Value("${omdb.api.base-url:https://www.omdbapi.com}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    @Override
    public String fetchPoster(String title, Integer year) {
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String url = baseUrl + "?apikey=" + apiKey + "&t=" + encodedTitle;
            if (year != null) {
                url += "&y=" + year;
            }
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) return null;
            Object poster = response.get("Poster");
            if (poster instanceof String s && !"N/A".equalsIgnoreCase(s)) {
                return s;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
