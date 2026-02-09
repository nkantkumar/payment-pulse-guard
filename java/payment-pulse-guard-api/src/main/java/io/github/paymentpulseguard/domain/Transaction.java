package io.github.paymentpulseguard.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String id;
    private String customerId;
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private TransactionType type;
    private String channel;
    private String beneficiaryId;
    private String beneficiaryName;
    private String beneficiaryCountry;
    private String ipAddress;
    private GeoLocation location;
    private String deviceId;
    private Instant timestamp;
    private String merchantCategory;
    private Map<String, Object> metadata;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT
    }
}
