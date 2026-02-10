# Deployment Guide

How to run Payment Pulse Guard locally, on Kubernetes, and on Google Cloud Run.

## Prerequisites

- **Java 24**, **Maven 3.9+**
- **Python 3.10**, **Poetry**
- **Docker** and **Docker Compose**
- Optional: **kubectl**, **gcloud** for K8s/Cloud Run

## Local (Docker Compose)

1. **Start infrastructure** (PostgreSQL, Kafka, Zookeeper, Redis, Elasticsearch):

   ```bash
   docker compose up -d
   ```

2. **Apply database schema** (run from repo root; uses PostgreSQL inside Docker so the `admin` user exists):

   ```bash
   docker compose exec -T postgres psql -U admin -d fraudaml < database/migrations/V001__initial_schema.sql
   ```

   If you use a local `psql` client instead, ensure it connects to the Docker Postgres (port 5432). A local PostgreSQL on your machine often has no `admin` role, which causes "role \"admin\" does not exist".

3. **Run ML Scoring Service** (Python):

   ```bash
   cd python/ml-scoring-service && poetry install && poetry run uvicorn app.main:app --host 0.0.0.0 --port 8000
   ```

4. **Build and run Java services** (from repo root):

   ```bash
   cd java && mvn -q clean install -DskipTests
   # Run each service (e.g. transaction-service on 8081, gateway on 8080)
   java -jar payment-pulse-guard-transaction-service/target/payment-pulse-guard-transaction-service-*.jar
   
   java -jar payment-pulse-guard-gateway/target/payment-pulse-guard-gateway-*.jar
   ```

   Or run from your IDE: `TransactionServiceApplication`, `GatewayApplication`, etc.
-------------sample for transaction service------------------
5. docker compose --profile full up -d
   {
   "amount": 1500.00,
   "currency": "USD",
   "type": "TRANSFER",
   "customerId": "cust_12344",
   "accountId": "acc_98765",
   "beneficiaryId": "ben_55555",
   "beneficiaryCountry": "US",
   "ipAddress": "192.168.1.100",
   "channel": "ONLINE",
   "timestamp": "2023-10-27T10:00:00Z"
   }
-----------------------------------

5. **Optional – run ML service in Docker** (with infra):

   ```bash
   docker compose --profile full up -d
   ```

### Environment (local)

- `KAFKA_BOOTSTRAP_SERVERS=localhost:9092`
- `POSTGRES_URL=jdbc:postgresql://localhost:5432/fraudaml`, `POSTGRES_USER=admin`, `POSTGRES_PASSWORD=password`
- `ML_SERVICE_URL=http://localhost:8000` (for transaction-service)

## Kubernetes

1. **Build and push images** (replace registry and tag as needed):

   ```bash
   # From repo root
   docker build -f java/payment-pulse-guard-transaction-service/Dockerfile -t your-registry/payment-pulse-guard/transaction-service:0.1.0 java
   docker build -f python/ml-scoring-service/Dockerfile -t your-registry/payment-pulse-guard/ml-scoring-service:0.1.0 python/ml-scoring-service
   docker push your-registry/payment-pulse-guard/transaction-service:0.1.0
   docker push your-registry/payment-pulse-guard/ml-scoring-service:0.1.0
   ```

2. **Create namespace and config**:

   ```bash
   kubectl apply -k deploy/kubernetes/
   ```

3. **Create secret for DB** (required by transaction-service):

   ```bash
   kubectl create secret generic payment-pulse-guard-db -n payment-pulse-guard \
     --from-literal=url='jdbc:postgresql://postgres-host:5432/fraudaml'
   ```

4. **Update image names** in `deploy/kubernetes/base/*.yaml` or use Kustomize `images` to point to your registry and tag.

5. **Deploy Kafka, PostgreSQL, Redis** (Helm or operator of choice) in the same or linked namespace and set `payment-pulse-guard-config` ConfigMap (e.g. `kafka.bootstrap.servers`) accordingly.

## Google Cloud Run

1. **Build and push** to Artifact Registry (see `deploy/cloudrun/README.md`).
2. **Deploy ML service**:

   ```bash
   gcloud run deploy ml-scoring-service \
     --image REGION-docker.pkg.dev/PROJECT_ID/REPO/ml-scoring-service:0.1.0 \
     --region REGION --platform managed --allow-unauthenticated --memory 1Gi
   ```

3. **Set `ML_SERVICE_URL`** for the Java transaction service to the Cloud Run URL of `ml-scoring-service`.
4. Use **VPC connector** and **Secret Manager** for Kafka, PostgreSQL, Redis in production.

## Health and readiness

- **Java**: `GET /actuator/health`
- **ML service**: `GET /health` (includes `models_loaded`)

For Kubernetes, readiness/liveness use these endpoints as in `deploy/kubernetes/base/*.yaml`.
