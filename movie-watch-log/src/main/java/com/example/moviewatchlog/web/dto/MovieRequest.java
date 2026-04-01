package com.example.moviewatchlog.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MovieRequest {

    @NotBlank
    private String title;

    private Integer watchedYear;

    private String watchedAt;

    private String source;
}
