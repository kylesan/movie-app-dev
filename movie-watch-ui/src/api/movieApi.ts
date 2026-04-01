// src/api/movieApi.ts
import { MOVIE_LOG_BASE_URL, MOVIE_EVENTS_BASE_URL } from "../config";

export interface Movie {
    id?: string;          // event / ES doc id (from movie-events-service)
    movieId?: string;     // stable movie id in movie-log-service
    title: string;
    watchedYear: number;
    watchedAt?: string;
    coverUrl?: string;
    source?: string;
    sourceUser?: string;
    ownerId?: string;
    streamingPlatforms?: string[];
}

export interface CreateMovieRequest {
    title: string;
    watchedYear: number;
}

export interface UpdateMovieRequest {
    title: string;
    watchedYear: number;
}

async function handleResponse<T>(res: Response): Promise<T> {
    if (!res.ok) {
        const text = await res.text();
        throw new Error(`HTTP ${res.status}: ${text}`);
    }
    // handle 204 (no content) gracefully
    if (res.status === 204) {
        return {} as T;
    }
    return res.json() as Promise<T>;
}

export async function createMovie(request: CreateMovieRequest): Promise<Movie> {
    const res = await fetch(`${MOVIE_LOG_BASE_URL}/api/movies`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(request),
    });

    return handleResponse<Movie>(res);
}

/**
 * Update an existing movie in the movie-watch-log service.
 * Now uses movieId, not the ES/event id.
 *
 * Assumes backend endpoint: PUT /api/movies/by-movie-id/{movieId}
 */
export async function updateMovie(
    movieId: string,
    request: UpdateMovieRequest
): Promise<Movie> {
    const res = await fetch(
        `${MOVIE_LOG_BASE_URL}/api/movies/by-movie-id/${encodeURIComponent(movieId)}`,
        {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(request),
        }
    );

    return handleResponse<Movie>(res);
}

/**
 * Delete a movie in the movie-watch-log service.
 * Now uses movieId, not the ES/event id.
 *
 * Assumes backend endpoint: DELETE /api/movies/by-movie-id/{movieId}
 */
export async function deleteMovie(movieId: string): Promise<void> {
    const res = await fetch(
        `${MOVIE_LOG_BASE_URL}/api/movies/by-movie-id/${encodeURIComponent(movieId)}`,
        {
            method: "DELETE",
        }
    );

    if (!res.ok) {
        const text = await res.text();
        throw new Error(`HTTP ${res.status}: ${text}`);
    }
}

/**
 * Uses the movie-events-service + Elasticsearch
 * to retrieve movies by watchedYear.
 *
 * These results include movieId, which you now use
 * for update/delete calls into movie-log-service.
 */
export async function searchMoviesByYear(year: number): Promise<Movie[]> {
    const res = await fetch(
        `${MOVIE_EVENTS_BASE_URL}/search/movies/by-year?year=${encodeURIComponent(
            year
        )}`
    );

    return handleResponse<Movie[]>(res);
}