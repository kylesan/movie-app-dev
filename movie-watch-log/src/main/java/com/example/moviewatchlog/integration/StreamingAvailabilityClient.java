package com.example.moviewatchlog.integration;

import java.util.List;

public interface StreamingAvailabilityClient {

    List<String> getStreamingPlatforms(String title, Integer year);
}
