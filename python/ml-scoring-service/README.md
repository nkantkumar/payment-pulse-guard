# ML Scoring Service

FastAPI service for fraud and AML risk scoring. Consumed by the Java transaction service.

## Requirements

- Python 3.10.x
- Poetry

## Setup

```bash
poetry install
```

## Run

```bash
poetry run uvicorn app.main:app --host 0.0.0.0 --port 8000
```

## Endpoints

- `POST /score` – Score a transaction (JSON body with transaction fields; camelCase from Java supported).
- `GET /health` – Health and model load status.
- `GET /models/info` – Model metadata.

## Models

Place trained joblib models in `models/`:

- `fraud_detector_v1.pkl`
- `aml_detector_v1.pkl`
- `scaler.pkl`

If absent, the service runs with stub scoring so the pipeline still works (e.g. for CI or first run).

## Docker

See repository root `docker-compose.yml` and `deploy/` for container and Kubernetes/Cloud Run deployment.
