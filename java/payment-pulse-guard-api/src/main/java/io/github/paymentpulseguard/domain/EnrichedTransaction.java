package io.github.paymentpulseguard.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class EnrichedTransaction extends Transaction {
    private CustomerProfile customerProfile;
    private Duration timeSinceLastTransaction;
    private BigDecimal avgAmount30d;
    private int transactionCount24h;
}
