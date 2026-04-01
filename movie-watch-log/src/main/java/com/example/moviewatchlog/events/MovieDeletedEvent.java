package com.example.moviewatchlog.events;

import lombok.Data;

@Data
public class MovieDeletedEvent {
    private String movieId;
}
