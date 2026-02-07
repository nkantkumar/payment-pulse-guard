# Payment Pulse Guard – Architecture

High-level architecture for the fraud and AML payment-scanning system, aligned with `initial-draft.pdf` and built for high volume, Kubernetes, and Cloud Run.

## System Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  Ingestion → Processing → AI/ML → Rules → Alerts → Case Management          │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Components

| Component | Responsibility | Tech |
|-----------|----------------|------|
| **API Gateway** | Routing, rate limiting, auth | Spring Cloud Gateway |
| **Transaction Service** | Consume transactions from Kafka, enrich, run rules, call ML, publish alerts | Spring Boot, Kafka, WebClient |
| **Customer Service** | Customer and risk profile; used for enrichment | Spring Boot, PostgreSQL, Redis |
| **Rules Engine** | Drools (or in-process) rules: high value, sanctions, rapid-fire, structuring | Drools / Java |
| **ML Scoring Service** | Fraud and AML risk scores; feature extraction and explainability | Python 3.10, FastAPI, Scikit-learn, XGBoost |
| **Alert Service** | Persist alerts, consume from Kafka, investigation workflow | Spring Boot, Kafka, PostgreSQL |
| **Case Service** | Case creation, SAR workflow, escalation | Spring Boot, PostgreSQL |
| **Enrichment Service** | External data (e.g. watchlists, geo); called by transaction service | Spring Boot |

## Data Flow

1. **Ingestion**: Transactions arrive via Kafka topic `transactions`.
2. **Transaction Service** (consumer):
   - Enriches via **Enrichment** and **Customer** (profile, aggregates).
   - Calls **ML Scoring Service** (HTTP) for fraud/AML score.
   - Evaluates **Rules Engine** (Drools or in-process).
   - If threshold or rule violation: builds **Alert** and publishes to Kafka topic `alerts`.
3. **Alert Service** consumes `alerts`, persists to PostgreSQL, exposes API for analysts.
4. **Case Service** creates and manages cases (e.g. from alerts), SAR filing, escalation.

## Scaling and High Volume

- **Targets** (from draft): 10k+ TPS, alert latency &lt; 500 ms, ML inference &lt; 100 ms.
- **Kafka**: Partition `transactions` by `customerId` or `accountId`; scale consumer group (transaction-service replicas).
- **ML service**: Stateless; scale horizontally (Kubernetes HPA or Cloud Run concurrency).
- **Caching**: Redis for customer/risk profiles and hot scores to reduce DB and ML load.
- **Database**: PostgreSQL with time-series partitioning on `transactions.timestamp`; read replicas for reporting.
- **Async**: ML call is async (CompletableFuture) so rule evaluation and I/O don’t block on network.

## Deployment

- **Kubernetes**: See `deploy/kubernetes/` (Kustomize). Run Kafka, PostgreSQL, Redis, Elasticsearch in-cluster or as managed services.
- **Cloud Run**: See `deploy/cloudrun/`. Deploy each service as a container; use VPC connector and Secret Manager for Kafka, DB, Redis.

## Compliance and Audit

- FATF/FinCEN-oriented rules (high value, sanctions, structuring).
- Full audit: log transaction analysis result, alert disposition, analyst actions, model version and decision rationale (e.g. ML explanation list).

## References

- `initial-draft.pdf` – Original design and examples.
- `README.md` – Repo layout and quick start.
- `docs/DEPLOYMENT.md` – Local, Kubernetes, and Cloud Run steps.
