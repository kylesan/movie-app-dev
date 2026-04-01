// src/App.tsx
import { AddMovieForm } from "./components/AddMovieForm";
import { MovieList } from "./components/MovieList";

function App() {
    return (
        <div
            style={{
                fontFamily: "system-ui, -apple-system, BlinkMacSystemFont, sans-serif",
                padding: "1.5rem",
                maxWidth: 1000,
                margin: "0 auto",
            }}
        >
            <h1>Movie Watch Log</h1>
            <p style={{ color: "#555", marginBottom: "1.5rem" }}>
                Add movies you’ve watched and view printable thumbnail grids by year.
            </p>

            <AddMovieForm />

            <MovieList />
        </div>
    );
}

export default App;
