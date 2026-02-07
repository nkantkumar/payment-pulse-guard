package io.github.paymentpulseguard.domain;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class Alert {
    private String id;
    private String transactionId;
    private String customerId;
    private String alertType;
    private Severity severity;
    private double riskScore;
    private List<String> violatedRules;
    private Map<String, Object> features;
    private Instant timestamp;
    private AlertStatus status;
    private String assignedTo;

    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum AlertStatus {
        NEW, INVESTIGATING, CLOSED
    }
}
