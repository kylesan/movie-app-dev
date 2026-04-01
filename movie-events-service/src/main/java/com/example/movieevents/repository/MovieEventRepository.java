package com.example.movieevents.repository;

import com.example.movieevents.document.MovieEventDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface MovieEventRepository extends ElasticsearchRepository<MovieEventDocument, String> {

    List<MovieEventDocument> findByWatchedYearOrderByWatchedAtAsc(Integer year);

    List<MovieEventDocument> findByMovieId(String movieId);
}