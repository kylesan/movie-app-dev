// src/components/AddMovieForm.tsx
import React, { useState } from "react";
import { createMovie, type CreateMovieRequest, type Movie } from "../api/movieApi";

interface AddMovieFormProps {
    onMovieCreated?: (movie: Movie) => void;
}

const currentYear = new Date().getFullYear();

export const AddMovieForm: React.FC<AddMovieFormProps> = ({ onMovieCreated }) => {
    const [title, setTitle] = useState("");
    const [year, setYear] = useState<number | "">(currentYear);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setSuccess(null);

        if (!title.trim()) {
            setError("Title is required.");
            return;
        }
        if (year === "" || Number.isNaN(Number(year))) {
            setError("Year is required.");
            return;
        }

        const payload: CreateMovieRequest = {
            title: title.trim(),
            watchedYear: Number(year),
        };

        try {
            setLoading(true);
            const saved = await createMovie(payload);
            setSuccess(`Saved "${saved.title}" (${saved.watchedYear}).`);
            setTitle("");
            setYear(currentYear);
            onMovieCreated?.(saved);
        } catch (err: any) {
            console.error(err);
            setError(err.message ?? "Failed to save movie.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <form
            onSubmit={handleSubmit}
            style={{
                display: "flex",
                flexDirection: "column",
                gap: "0.5rem",
                maxWidth: 400,
            }}
        >
            <h2>Add Watched Movie</h2>

            <label>
                Title
                <input
                    type="text"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    placeholder="e.g. Akira"
                    style={{ width: "100%", padding: "0.4rem" }}
                />
            </label>

            <label>
                Year Watched
                <input
                    type="number"
                    value={year}
                    onChange={(e) =>
                        setYear(e.target.value === "" ? "" : Number(e.target.value))
                    }
                    style={{ width: "100%", padding: "0.4rem" }}
                />
            </label>

            <button
                type="submit"
                disabled={loading}
                style={{ padding: "0.5rem", cursor: loading ? "wait" : "pointer" }}
            >
                {loading ? "Saving..." : "Save Movie"}
            </button>

            {error && <div style={{ color: "red" }}>{error}</div>}
            {success && <div style={{ color: "green" }}>{success}</div>}
        </form>
    );
};
