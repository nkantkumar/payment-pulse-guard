-- Payment Pulse Guard - Initial PostgreSQL schema
-- Aligned with initial-draft.pdf (transactions, alerts, cases, customer_risk_profiles, model_performance)

-- Transactions
CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    account_id VARCHAR(50) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    beneficiary_id VARCHAR(50),
    beneficiary_name VARCHAR(200),
    beneficiary_country VARCHAR(2),
    ip_address VARCHAR(45),
    device_id VARCHAR(100),
    merchant_category VARCHAR(50),
    timestamp TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_transactions_customer ON transactions (customer_id);
CREATE INDEX IF NOT EXISTS idx_transactions_timestamp ON transactions (timestamp);
CREATE INDEX IF NOT EXISTS idx_transactions_amount ON transactions (amount);

-- Outbox Events (Transactional Outbox Pattern)


CREATE TABLE IF NOT EXISTS outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_outbox_processed ON outbox_events (processed_at);

-- Alerts
CREATE TABLE IF NOT EXISTS alerts (
    id UUID PRIMARY KEY,
    transaction_id UUID REFERENCES transactions(id),
    customer_id VARCHAR(50) NOT NULL,
    alert_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    risk_score DECIMAL(5,4) NOT NULL,
    violated_rules JSONB,
    ml_features JSONB,
    status VARCHAR(20) NOT NULL,
    assigned_to VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    closed_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_alerts_status ON alerts (status);
CREATE INDEX IF NOT EXISTS idx_alerts_customer ON alerts (customer_id);
CREATE INDEX IF NOT EXISTS idx_alerts_risk_score ON alerts (risk_score DESC);

-- Cases
CREATE TABLE IF NOT EXISTS cases (
    id UUID PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    customer_name VARCHAR(200),
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    escalated BOOLEAN DEFAULT FALSE,
    escalation_reason TEXT,
    resolution VARCHAR(20),
    resolution_notes TEXT,
    sar_filed BOOLEAN DEFAULT FALSE,
    sar_id VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    closed_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_cases_status ON cases (status);
CREATE INDEX IF NOT EXISTS idx_cases_customer ON cases (customer_id);

-- Customer risk profiles (cache / aggregated view)
CREATE TABLE IF NOT EXISTS customer_risk_profiles (
    customer_id VARCHAR(50) PRIMARY KEY,
    base_risk_score DECIMAL(5,4) NOT NULL,
    transaction_count_30d INT,
    avg_transaction_amount DECIMAL(15,2),
    max_transaction_amount DECIMAL(15,2),
    high_risk_countries JSONB,
    kyc_status VARCHAR(20),
    pep_status BOOLEAN DEFAULT FALSE,
    sanction_hit BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Model performance (ML metrics for monitoring)
CREATE TABLE IF NOT EXISTS model_performance (
    id SERIAL PRIMARY KEY,
    model_name VARCHAR(50) NOT NULL,
    model_version VARCHAR(20) NOT NULL,
    precision_score DECIMAL(5,4),
    recall DECIMAL(5,4),
    f1_score DECIMAL(5,4),
    auc_roc DECIMAL(5,4),
    false_positive_rate DECIMAL(5,4),
    evaluation_date TIMESTAMP DEFAULT NOW()
);
