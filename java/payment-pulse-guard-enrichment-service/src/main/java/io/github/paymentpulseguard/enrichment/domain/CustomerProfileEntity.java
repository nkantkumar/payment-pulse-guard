package io.github.paymentpulseguard.enrichment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "customer_risk_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileEntity {

    @Id
    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "base_risk_score")
    private BigDecimal baseRiskScore;

    @Column(name = "transaction_count_30d")
    private Integer transactionCount30d;

    @Column(name = "avg_transaction_amount")
    private BigDecimal avgTransactionAmount;

    @Column(name = "max_transaction_amount")
    private BigDecimal maxTransactionAmount;

    @Column(name = "high_risk_countries", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String highRiskCountries;

    @Column(name = "kyc_status")
    private String kycStatus;

    @Column(name = "pep_status")
    private Boolean pepStatus;

    @Column(name = "sanction_hit")
    private Boolean sanctionHit;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
