package io.github.paymentpulseguard.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EnrichedTransaction extends Transaction {
    private CustomerProfile customerProfile;
    private Duration timeSinceLastTransaction;
    private BigDecimal avgAmount30d;
    private int transactionCount24h;
}
