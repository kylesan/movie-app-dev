package com.example.movieevents.web;

import com.example.movieevents.search.MovieSearchDocument;
import com.example.movieevents.search.MovieSearchRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search/movies")
public class MovieSearchController {

    private final MovieSearchRepository repository;

    public MovieSearchController(MovieSearchRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/by-year")
    public List<MovieSearchDocument> byYear(@RequestParam Integer year) {
        // no auth for now; later we’ll add ownerId filter
        return repository.findByWatchedYearOrderByWatchedAtAsc(year);
    }

    @GetMapping("/by-title")
    public List<MovieSearchDocument> byTitle(@RequestParam String q) {
        return repository.findByTitleContainingIgnoreCaseOrderByWatchedAtAsc(q);
    }

    @GetMapping("/by-platform")
    public List<MovieSearchDocument> byPlatform(
            @RequestParam String platform,
            @RequestParam Integer year
    ) {
        return repository.findByStreamingPlatformsContainsAndWatchedYearOrderByWatchedAtAsc(
                platform, year
        );
    }
}
