# Google Cloud Run Deployment

Deploy Payment Pulse Guard services to Cloud Run (fully managed or on GKE).

## Prerequisites

- Google Cloud SDK (`gcloud`)
- Docker (for building images)
- Artifact Registry or Container Registry

## Build and push images

```bash
# Set your project and region
export PROJECT_ID=your-gcp-project
export REGION=us-central1
export REPO=payment-pulse-guard

# Create Artifact Registry repo (once)
gcloud artifacts repositories create $REPO --repository-format=docker --location=$REGION

# ML Scoring Service (Python)
docker build -t $REGION-docker.pkg.dev/$PROJECT_ID/$REPO/ml-scoring-service:0.1.0 \
  -f ../../python/ml-scoring-service/Dockerfile ../../python/ml-scoring-service
docker push $REGION-docker.pkg.dev/$PROJECT_ID/$REPO/ml-scoring-service:0.1.0

# Transaction Service (Java) – build from repo root with Maven first
# Then build image and push similarly.
```

## Deploy ML Scoring Service to Cloud Run

```bash
gcloud run deploy ml-scoring-service \
  --image $REGION-docker.pkg.dev/$PROJECT_ID/$REPO/ml-scoring-service:0.1.0 \
  --region $REGION \
  --platform managed \
  --allow-unauthenticated \
  --memory 1Gi \
  --cpu 1 \
  --min-instances 0 \
  --max-instances 10
```

## Connect to Kafka/PostgreSQL/Redis

Use Cloud Run VPC connector to reach Kafka (e.g. Confluent Cloud or self-hosted), Cloud SQL (PostgreSQL), and Memorystore (Redis). Set environment variables or Secret Manager for:

- `KAFKA_BOOTSTRAP_SERVERS`
- `POSTGRES_URL`, `POSTGRES_USER`, `POSTGRES_PASSWORD`
- `ML_SERVICE_URL` (for Java services: use the Cloud Run URL of ml-scoring-service)

## High volume

- Set `--min-instances` and `--max-instances` as needed; use CPU/memory sizing and request timeouts.
- For 10k+ TPS, consider GKE with Kafka and horizontal scaling of consumers and ML replicas.
