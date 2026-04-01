# movie-watch-log

Spring Boot 3 service for logging watched movies.

Features:
- MongoDB persistence
- Per-user ownership via JWT (Keycloak-friendly)
- Fetches cover URL from OMDb
- Fetches streaming platforms from TMDB
- CRUD REST API at `/api/movies`
- Admin-only PUT/DELETE based on `admin` role in JWT
