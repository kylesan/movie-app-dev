package com.example.movieevents.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MovieSearchRepository extends ElasticsearchRepository<MovieSearchDocument, String> {

    List<MovieSearchDocument> findByOwnerIdAndWatchedYearOrderByWatchedAtAsc(
            String ownerId, Integer watchedYear);

    List<MovieSearchDocument> findByOwnerIdAndTitleContainingIgnoreCaseOrderByWatchedAtAsc(
            String ownerId, String titlePart);

    List<MovieSearchDocument> findByOwnerIdAndStreamingPlatformsContainsAndWatchedYearOrderByWatchedAtAsc(
            String ownerId, String platform, Integer watchedYear);

    // Admin-wide variants
    List<MovieSearchDocument> findByWatchedYearOrderByWatchedAtAsc(Integer watchedYear);

    List<MovieSearchDocument> findByTitleContainingIgnoreCaseOrderByWatchedAtAsc(String titlePart);

    List<MovieSearchDocument> findByStreamingPlatformsContainsAndWatchedYearOrderByWatchedAtAsc(
            String platform, Integer watchedYear);
}