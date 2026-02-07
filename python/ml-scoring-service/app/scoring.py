"""
Scoring logic: feature extraction, model inference, explainability.
Uses stub scores when models are not present (e.g. in CI or first run).
"""
from pathlib import Path
from typing import Any

from app.schemas import MLScore, Transaction

# Module-level model refs (set by load_models)
_fraud_model = None
_aml_model = None
_scaler = None
FEATURE_NAMES = [
    "amount", "hour", "day_of_week", "customer_age",
    "avg_amount_30d", "tx_count_24h", "customer_risk",
    "amount_ratio", "unusual_hours", "country_mismatch",
]


def load_models(model_dir: Path) -> None:
    """Load joblib models from directory. Raises if files missing."""
    import joblib
    global _fraud_model, _aml_model, _scaler
    _fraud_model = joblib.load(model_dir / "fraud_detector_v1.pkl")
    _aml_model = joblib.load(model_dir / "aml_detector_v1.pkl")
    _scaler = joblib.load(model_dir / "scaler.pkl")


def extract_features(tx: Transaction) -> list[float]:
    """Engineer features for ML models."""
    amount_ratio = tx.amount / (tx.avg_transaction_amount_30d + 1.0)
    unusual_hours = 1 if (tx.hour_of_day < 6 or tx.hour_of_day > 22) else 0
    country_mismatch = 1 if (tx.beneficiary_country and tx.beneficiary_country != tx.ip_country) else 0
    return [
        tx.amount,
        float(tx.hour_of_day),
        float(tx.day_of_week),
        float(tx.customer_age_days),
        tx.avg_transaction_amount_30d,
        float(tx.transaction_count_24h),
        tx.customer_risk_score,
        amount_ratio,
        float(unusual_hours),
        float(country_mismatch),
        1.0 if tx.transaction_type == "WITHDRAWAL" else 0.0,
        1.0 if tx.transaction_type == "TRANSFER" else 0.0,
        1.0 if tx.channel == "ONLINE" else 0.0,
        1.0 if tx.channel == "ATM" else 0.0,
    ]


def get_feature_importance(features: list[float]) -> dict[str, float]:
    """Return normalized feature contributions for explainability."""
    import numpy as np
    n = len(FEATURE_NAMES)
    # Placeholder: equal weight or use first n features
    importance = [1.0 / max(n, 1)] * min(n, len(features))
    total = sum(importance)
    if total <= 0:
        total = 1.0
    return {name: round(imp / total, 4) for name, imp in zip(FEATURE_NAMES, importance)}


def generate_explanation(
    tx: Transaction,
    fraud_score: float,
    aml_score: float,
    features: dict[str, float],
) -> list[str]:
    """Human-readable explanation of the score."""
    explanations = []
    if fraud_score > 0.7:
        explanations.append("High fraud risk detected")
    if features.get("amount_ratio", 0) > 0.15:
        explanations.append("Transaction amount significantly exceeds customer norm")
    if features.get("unusual_hours", 0) > 0.1:
        explanations.append("Transaction at unusual time")
    if tx.transaction_count_24h > 10:
        explanations.append(f"High transaction velocity: {tx.transaction_count_24h} in 24h")
    if aml_score > 0.7:
        explanations.append("High AML risk detected")
    if features.get("country_mismatch", 0) > 0.1:
        explanations.append("IP location differs from beneficiary country")
    if not explanations:
        explanations.append("No significant risk factors detected")
    return explanations


def score_transaction(tx: Transaction) -> MLScore:
    """Score a transaction; uses stub when models are not loaded."""
    features = extract_features(tx)
    feature_dict = get_feature_importance(features)

    if _fraud_model is not None and _aml_model is not None and _scaler is not None:
        import numpy as np
        X = _scaler.transform([features])
        fraud_prob = float(_fraud_model.predict_proba(X)[0, 1])
        aml_prob = float(_aml_model.predict_proba(X)[0, 1])
    else:
        # Stub: simple heuristic so Java integration works without trained models
        fraud_prob = min(0.95, 0.2 + (tx.amount / 20_000.0) + (0.1 if tx.transaction_count_24h > 5 else 0))
        aml_prob = min(0.95, 0.15 + (0.3 if tx.beneficiary_country != tx.ip_country else 0))

    combined = round(0.6 * fraud_prob + 0.4 * aml_prob, 4)
    fraud_prob = round(fraud_prob, 4)
    aml_prob = round(aml_prob, 4)
    explanation = generate_explanation(tx, fraud_prob, aml_prob, feature_dict)

    return MLScore(
        transaction_id=tx.id,
        fraud_score=fraud_prob,
        aml_score=aml_prob,
        combined_score=combined,
        features=feature_dict,
        model_version="v1.0.0",
        explanation=explanation,
    )
