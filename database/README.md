# Database

PostgreSQL schema and migrations for Payment Pulse Guard.

## Schema

See `migrations/` for versioned SQL. Apply in order when bringing up a new environment.

## Quick apply (development)

From the repo root, with Docker Compose Postgres running:

```bash
docker compose exec -T postgres psql -U postgres -d fraudaml < database/migrations/V001__initial_schema.sql
```

Using a local `psql` client: ensure it connects to the same Postgres (port 5432); the Docker container creates user `admin` with password `password`.

For production use a migration tool (Flyway, Liquibase) or your platform’s migration pipeline.
