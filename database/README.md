# Database

PostgreSQL schema and migrations for Payment Pulse Guard.

## Schema

See `migrations/` for versioned SQL. Apply in order when bringing up a new environment.

## Quick apply (development)

```bash
psql -h localhost -U admin -d fraudaml -f migrations/V001__initial_schema.sql
```

For production use a migration tool (Flyway, Liquibase) or your platform’s migration pipeline.
