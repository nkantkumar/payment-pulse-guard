package io.github.paymentpulseguard.ml;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MLTransactionRequest {
    private String id;
    private double amount;
    private String customerId;
    private String transactionType;
    private String channel;
    private String beneficiaryCountry;
    private int hourOfDay;
    private int dayOfWeek;
    private int customerAgeDays;
    private double avgTransactionAmount30d;
    private int transactionCount24h;
    private double customerRiskScore;
    private String ipCountry;
    private String deviceFingerprint;
}
