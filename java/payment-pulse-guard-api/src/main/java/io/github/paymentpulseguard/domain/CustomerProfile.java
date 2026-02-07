package io.github.paymentpulseguard.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class CustomerProfile {
    private String customerId;
    private String homeCountry;
    private int ageDays;
    private BigDecimal avgAmount30d;
    private int transactionCount24h;
    private BigDecimal riskScore;
    private Instant updatedAt;
}
