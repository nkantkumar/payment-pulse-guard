# Payment Pulse Guard

A high-volume, open-source **Fraud and AML (Anti-Money Laundering)** detection system for payment scanning. It combines rule-based detection (FATF/FinCEN-aligned) with AI/ML models for anomaly detection, risk scoring, and behavioral analysis.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  Ingestion → Processing → AI/ML → Rules → Alerts → Case Management          │
└─────────────────────────────────────────────────────────────────────────────┘
```

- **Data ingestion**: Real-time transaction streaming (Kafka), batch customer data, external enrichment
- **AI/ML layer**: Anomaly detection, pattern recognition, risk scoring, behavioral profiling
- **Rules engine**: Regulatory compliance, threshold alerts, watchlist/sanctions screening
- **Case management**: Alert investigation, SAR workflow, analyst tools

## Technology Stack

| Layer        | Stack |
|-------------|--------|
| **Runtime** | Java 24, Python 3.10 |
| **Build**   | Maven (Java), Poetry (Python) |
| **Java**    | Spring Boot 3.x, Spring Cloud, Apache Kafka, Drools |
| **Python**  | FastAPI, Scikit-learn, XGBoost, MLflow |
| **Data**    | PostgreSQL, Redis, Elasticsearch |
| **Deploy**  | Docker, Kubernetes, Google Cloud Run |

## Repository Layout

```
payment-pulse-guard/
├── java/                    # Maven multi-module (Java 24)
│   ├── payment-pulse-guard-api/           # Shared DTOs, events
│   ├── payment-pulse-guard-gateway/       # API Gateway
│   ├── payment-pulse-guard-transaction-service/
│   ├── payment-pulse-guard-customer-service/
│   ├── payment-pulse-guard-rules-engine/
│   ├── payment-pulse-guard-alert-service/
│   ├── payment-pulse-guard-case-service/
│   └── payment-pulse-guard-enrichment-service/
├── python/
│   └── ml-scoring-service/   # FastAPI ML scoring (Poetry)
├── deploy/
│   ├── kubernetes/           # K8s manifests and Kustomize
│   └── cloudrun/             # Cloud Run service definitions
├── database/                 # Schema and migrations
└── docs/                     # Architecture and runbooks
```

## Quick Start

### Prerequisites

- **Java 24**, **Maven 3.9+**
- **Python 3.10**, **Poetry**
- **Docker** and **Docker Compose** (for local stack)
- Optional: **kubectl**, **gcloud** for K8s/Cloud Run

### Run infrastructure (Kafka, PostgreSQL, Redis, etc.)

```bash
docker compose up -d
```

### Build and run Java services

```bash
cd java && mvn -q clean install -DskipTests
# Run individual services or use your IDE; see docs/DEPLOYMENT.md
```

### Run Python ML scoring service

```bash
cd python/ml-scoring-service && poetry install && poetry run uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

See [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) for full local, Kubernetes, and Cloud Run instructions.

## Performance Targets

- **Transaction throughput**: 10,000+ TPS
- **Alert latency**: &lt; 500 ms
- **ML inference**: &lt; 100 ms
- **Dashboard**: Real-time (WebSocket)

## Compliance & Regulations

Designed to support:

- FATF (Financial Action Task Force)
- Bank Secrecy Act (BSA) / FinCEN reporting
- GDPR (EU), PCI DSS (payment data)

Audit trail: transaction analysis, alert dispositions, analyst actions, model versions, and decision rationale are logged and traceable.

## Documentation

- [Architecture](docs/ARCHITECTURE.md) – Components, data flow, scaling
- [Deployment](docs/DEPLOYMENT.md) – Local, Kubernetes, Cloud Run
- [Initial design draft](initial-draft.pdf) – Original system design

## Contributing

Contributions are welcome. Please read our contributing guidelines and open issues/PRs on GitHub. This project is licensed under the [Apache License 2.0](LICENSE).
