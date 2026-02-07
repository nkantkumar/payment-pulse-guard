"""Pydantic schemas for ML scoring API."""
from typing import Any

from pydantic import BaseModel, Field


class Transaction(BaseModel):
    """Transaction payload from Java transaction service (camelCase from Java)."""
    id: str
    amount: float = 0.0
    customer_id: str = Field(default="", alias="customerId")
    transaction_type: str = Field(default="", alias="transactionType")
    channel: str = ""
    beneficiary_country: str = Field(default="", alias="beneficiaryCountry")
    hour_of_day: int = Field(default=0, alias="hourOfDay")
    day_of_week: int = Field(default=0, alias="dayOfWeek")
    customer_age_days: int = Field(default=0, alias="customerAgeDays")
    avg_transaction_amount_30d: float = Field(default=0.0, alias="avgTransactionAmount30d")
    transaction_count_24h: int = Field(default=0, alias="transactionCount24h")
    customer_risk_score: float = Field(default=0.0, alias="customerRiskScore")
    ip_country: str = Field(default="", alias="ipCountry")
    device_fingerprint: str = Field(default="", alias="deviceFingerprint")

    model_config = {"populate_by_name": True, "extra": "ignore"}


class MLScore(BaseModel):
    """ML scoring response; consumed by Java (camelCase for transactionId, etc.)."""
    transaction_id: str = Field(alias="transactionId")
    fraud_score: float = Field(ge=0, le=1, alias="fraudScore")
    aml_score: float = Field(ge=0, le=1, alias="amlScore")
    combined_score: float = Field(ge=0, le=1, alias="combinedScore")
    features: dict[str, float] = Field(default_factory=dict)
    model_version: str = Field(default="v1.0.0", alias="modelVersion")
    explanation: list[str] = Field(default_factory=list)

    model_config = {"populate_by_name": True, "ser_json_by_alias": True}
