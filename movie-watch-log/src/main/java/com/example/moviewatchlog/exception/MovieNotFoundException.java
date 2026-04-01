package com.example.moviewatchlog.exception;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(String id) {
        super("Movie not found: " + id);
    }
}
