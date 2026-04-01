// src/components/MovieList.tsx
import React, { useState } from "react";
import {
    type Movie,
    searchMoviesByYear,
    updateMovie,
    deleteMovie,
} from "../api/movieApi";

export const MovieList: React.FC = () => {
    const [year, setYear] = useState<number | "">("");
    const [movies, setMovies] = useState<Movie[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const [editingId, setEditingId] = useState<string | null>(null);
    const [editTitle, setEditTitle] = useState("");
    const [editYear, setEditYear] = useState<number | "">("");

    const handleSearch = async () => {
        if (year === "" || Number.isNaN(Number(year))) {
            setError("Enter a year to search.");
            return;
        }
        setError(null);
        setLoading(true);
        try {
            const results = await searchMoviesByYear(Number(year));
            setMovies(results);
        } catch (err: any) {
            console.error(err);
            setError(err.message ?? "Failed to load movies.");
        } finally {
            setLoading(false);
        }
    };

    const startEditing = (movie: Movie) => {
        if (!movie.movieId) {
            setError("This movie doesn't have an id; cannot edit.");
            return;
        }
        setEditingId(movie.movieId);
        setEditTitle(movie.title);
        setEditYear(movie.watchedYear);
        setError(null);
    };

    const cancelEditing = () => {
        setEditingId(null);
        setEditTitle("");
        setEditYear("");
    };

    const saveEdit = async () => {
        if (!editingId) return;
        if (!editTitle.trim()) {
            setError("Title is required.");
            return;
        }
        if (editYear === "" || Number.isNaN(Number(editYear))) {
            setError("Year is required.");
            return;
        }

        try {
            setLoading(true);
            setError(null);
            const updated = await updateMovie(editingId, {
                title: editTitle.trim(),
                watchedYear: Number(editYear),
            });

            // Update local state
            setMovies((prev) =>
                prev.map((m) => (m.movieId === editingId ? { ...m, ...updated } : m))
            );

            cancelEditing();
        } catch (err: any) {
            console.error(err);
            setError(err.message ?? "Failed to update movie.");
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (movie: Movie) => {
        if (!movie.movieId) {
            setError("This movie doesn't have an id; cannot delete.");
            return;
        }
        const confirmed = window.confirm(
            `Delete "${movie.title}" (${movie.watchedYear})?`
        );
        if (!confirmed) return;

        try {
            setLoading(true);
            setError(null);
            await deleteMovie(movie.movieId);
            setMovies((prev) => prev.filter((m) => m.movieId !== movie.movieId));
        } catch (err: any) {
            console.error(err);
            setError(err.message ?? "Failed to delete movie.");
        } finally {
            setLoading(false);
        }
    };

    const handlePrint = () => {
        window.print();
    };

    return (
        <div style={{ marginTop: "2rem" }}>
            <h2>Movies Watched by Year</h2>

            <div style={{ display: "flex", gap: "0.5rem", alignItems: "center" }}>
                <input
                    type="number"
                    placeholder="Year"
                    value={year}
                    onChange={(e) =>
                        setYear(e.target.value === "" ? "" : Number(e.target.value))
                    }
                    style={{ padding: "0.4rem" }}
                />
                <button
                    type="button"
                    onClick={handleSearch}
                    disabled={loading}
                    style={{ padding: "0.4rem 0.8rem" }}
                >
                    {loading ? "Loading..." : "Search"}
                </button>
                {movies.length > 0 && (
                    <button
                        type="button"
                        onClick={handlePrint}
                        style={{ padding: "0.4rem 0.8rem" }}
                    >
                        Print Thumbnails
                    </button>
                )}
            </div>

            {error && <div style={{ color: "red", marginTop: "0.5rem" }}>{error}</div>}

            <div
                style={{
                    marginTop: "1rem",
                    display: "grid",
                    gridTemplateColumns: "repeat(auto-fill, minmax(180px, 1fr))",
                    gap: "1rem",
                }}
            >
                {movies.map((m) => {
                    const isEditing = m.movieId && m.movieId === editingId;

                    return (
                        <div
                            key={m.movieId ?? m.id ?? `${m.title}-${m.watchedAt}`}
                            style={{
                                border: "1px solid #ccc",
                                borderRadius: 4,
                                padding: "0.5rem",
                                textAlign: "center",
                            }}
                        >
                            {/* Thumbnail */}
                            {m.coverUrl ? (
                                <img
                                    src={m.coverUrl}
                                    alt={m.title}
                                    style={{
                                        width: "100%",
                                        height: 200,
                                        objectFit: "cover",
                                        marginBottom: "0.5rem",
                                    }}
                                />
                            ) : (
                                <div
                                    style={{
                                        width: "100%",
                                        height: 200,
                                        marginBottom: "0.5rem",
                                        background: "#eee",
                                        display: "flex",
                                        alignItems: "center",
                                        justifyContent: "center",
                                        fontSize: "0.8rem",
                                        padding: "0.5rem",
                                    }}
                                >
                                    No cover image
                                </div>
                            )}

                            {/* Content */}
                            {isEditing ? (
                                <div style={{ textAlign: "left" }}>
                                    <div style={{ marginBottom: "0.25rem" }}>
                                        <label style={{ fontSize: "0.8rem" }}>
                                            Title
                                            <input
                                                type="text"
                                                value={editTitle}
                                                onChange={(e) => setEditTitle(e.target.value)}
                                                style={{
                                                    width: "100%",
                                                    padding: "0.3rem",
                                                    marginTop: "0.15rem",
                                                }}
                                            />
                                        </label>
                                    </div>
                                    <div style={{ marginBottom: "0.25rem" }}>
                                        <label style={{ fontSize: "0.8rem" }}>
                                            Year
                                            <input
                                                type="number"
                                                value={editYear}
                                                onChange={(e) =>
                                                    setEditYear(
                                                        e.target.value === ""
                                                            ? ""
                                                            : Number(e.target.value)
                                                    )
                                                }
                                                style={{
                                                    width: "100%",
                                                    padding: "0.3rem",
                                                    marginTop: "0.15rem",
                                                }}
                                            />
                                        </label>
                                    </div>
                                </div>
                            ) : (
                                <>
                                    <div style={{ fontWeight: "bold" }}>{m.title}</div>
                                    <div style={{ fontSize: "0.8rem", color: "#555" }}>
                                        {m.watchedYear}
                                    </div>
                                    {m.streamingPlatforms &&
                                        m.streamingPlatforms.length > 0 && (
                                            <div
                                                style={{
                                                    fontSize: "0.7rem",
                                                    color: "#333",
                                                    marginTop: "0.25rem",
                                                }}
                                            >
                                                <strong>Streaming:</strong>{" "}
                                                {m.streamingPlatforms.join(", ")}
                                            </div>
                                        )}
                                </>
                            )}

                            {/* Actions */}
                            <div
                                style={{
                                    marginTop: "0.5rem",
                                    display: "flex",
                                    justifyContent: "center",
                                    gap: "0.5rem",
                                }}
                            >
                                {isEditing ? (
                                    <>
                                        <button
                                            type="button"
                                            onClick={saveEdit}
                                            disabled={loading}
                                            style={{ padding: "0.25rem 0.5rem" }}
                                        >
                                            Save
                                        </button>
                                        <button
                                            type="button"
                                            onClick={cancelEditing}
                                            disabled={loading}
                                            style={{ padding: "0.25rem 0.5rem" }}
                                        >
                                            Cancel
                                        </button>
                                    </>
                                ) : (
                                    <>
                                        <button
                                            type="button"
                                            onClick={() => startEditing(m)}
                                            disabled={loading || !m.movieId}
                                            style={{ padding: "0.25rem 0.5rem" }}
                                        >
                                            Edit
                                        </button>
                                        <button
                                            type="button"
                                            onClick={() => handleDelete(m)}
                                            disabled={loading || !m.movieId}
                                            style={{ padding: "0.25rem 0.5rem" }}
                                        >
                                            Delete
                                        </button>
                                    </>
                                )}
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};
