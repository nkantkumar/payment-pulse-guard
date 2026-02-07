"""
Fraud and AML ML Scoring Service (FastAPI).
Scores transactions for fraud and AML risk; used by the Java transaction service.
"""
import logging
from pathlib import Path

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware

from app.schemas import MLScore, Transaction as TxSchema
from app.scoring import score_transaction, load_models

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="Payment Pulse Guard - ML Scoring Service",
    description="Fraud and AML risk scoring for transactions",
    version="0.1.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Load models on startup (optional; can lazy-load)
MODELS_LOADED = False


@app.on_event("startup")
async def startup():
    global MODELS_LOADED
    model_dir = Path(__file__).resolve().parent.parent / "models"
    try:
        load_models(model_dir)
        MODELS_LOADED = True
    except FileNotFoundError as e:
        logger.warning("Models not found at %s; scoring will use stub. %s", model_dir, e)
        MODELS_LOADED = False


@app.post("/score", response_model=MLScore)
async def score(tx: TxSchema) -> MLScore:
    """Score a transaction for fraud and AML risk."""
    try:
        return score_transaction(tx)
    except Exception as e:
        logger.exception("Error scoring transaction %s", tx.id)
        raise HTTPException(status_code=500, detail=str(e)) from e


@app.get("/health")
async def health():
    return {"status": "healthy", "models_loaded": MODELS_LOADED}


@app.get("/models/info")
async def model_info():
    return {
        "fraud_model": "RandomForestClassifier (or stub)",
        "aml_model": "XGBoostClassifier (or stub)",
        "version": "v1.0.0",
        "models_loaded": MODELS_LOADED,
    }
