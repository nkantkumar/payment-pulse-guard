package io.github.paymentpulseguard.alert.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertEntity {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "transaction_id")
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID transactionId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "alert_type")
    private String alertType;

    private String severity;

    @Column(name = "risk_score")
    private BigDecimal riskScore;

    @Column(name = "violated_rules", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String violatedRules;

    @Column(name = "ml_features", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String mlFeatures;

    private String status;

    @Column(name = "assigned_to")
    private String assignedTo;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "closed_at")
    private Instant closedAt;
}
