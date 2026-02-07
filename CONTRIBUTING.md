# Contributing to Payment Pulse Guard

Thank you for your interest in contributing. This project follows the design in `initial-draft.pdf` and aims to be open-source friendly and high-volume ready.

## Development setup

- **Java**: 24 (Eclipse Temurin or OpenJDK). Build with Maven from the `java/` directory.
- **Python**: 3.10.x. Use Poetry in `python/ml-scoring-service/`.
- **Infrastructure**: Start PostgreSQL, Kafka, Redis (and optionally Elasticsearch) via `docker compose up -d`, then apply `database/migrations/`.

## Code style

- **Java**: Standard Java 24 style; prefer records where appropriate. Use the existing package layout (`io.github.paymentpulseguard.*`).
- **Python**: PEP 8; type hints preferred. Use the existing `app/` layout.

## Testing

- **Java**: `mvn test` in the relevant module or from `java/`.
- **Python**: `poetry run pytest` in `python/ml-scoring-service/`.

## Submitting changes

1. Open an issue or pick an existing one.
2. Fork the repo, create a branch, make your changes.
3. Ensure builds and tests pass.
4. Open a pull request with a clear description and reference to the issue.

## License

By contributing, you agree that your contributions will be licensed under the Apache License 2.0.
