package com.example.moviewatchlog.web;

import com.example.moviewatchlog.model.Movie;
import com.example.moviewatchlog.service.MovieService;
import com.example.moviewatchlog.web.dto.MovieRequest;
import com.example.moviewatchlog.web.dto.MovieResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public MovieResponse create(@Valid @RequestBody MovieRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setMovieId(UUID.randomUUID().toString());
        movie.setWatchedYear(request.getWatchedYear());
        movie.setWatchedAt(request.getWatchedAt());
        movie.setSource(request.getSource());
        Movie saved = movieService.createMovie(movie);
        return toResponse(saved);
    }

    @GetMapping
    public List<MovieResponse> byYear(@RequestParam Integer year) {
        return movieService.getMoviesByYear(year).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public MovieResponse get(@PathVariable String id) {
        return toResponse(movieService.getMovie(id));
    }

    @PutMapping("/{id}")
    public MovieResponse update(@PathVariable String id,
                                @Valid @RequestBody MovieRequest request) {
        Movie updated = new Movie();
        updated.setTitle(request.getTitle());
        updated.setWatchedYear(request.getWatchedYear());
        updated.setWatchedAt(request.getWatchedAt());
        updated.setSource(request.getSource());
        Movie saved = movieService.updateMovie(id, updated);
        return toResponse(saved);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        movieService.deleteMovie(id);
    }

    private MovieResponse toResponse(Movie movie) {
        MovieResponse dto = new MovieResponse();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setWatchedYear(movie.getWatchedYear());
        dto.setWatchedAt(movie.getWatchedAt());
        dto.setCoverUrl(movie.getCoverUrl());
        dto.setSource(movie.getSource());
        dto.setSourceUser(movie.getSourceUser());
        dto.setStreamingPlatforms(movie.getStreamingPlatforms());
        return dto;
    }
}
