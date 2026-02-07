package io.github.paymentpulseguard.transaction.monitoring;

import io.github.paymentpulseguard.domain.CustomerProfile;
import io.github.paymentpulseguard.domain.EnrichedTransaction;
import io.github.paymentpulseguard.domain.GeoLocation;
import io.github.paymentpulseguard.domain.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

/**
 * Enriches transaction with customer profile and aggregated metrics.
 * In production: call customer-service and cache (Redis); compute time-since-last from stream state.
 */
@Service
public class EnrichmentService {

    public EnrichedTransaction enrich(Transaction transaction) {
        CustomerProfile profile = resolveProfile(transaction.getCustomerId());
        return EnrichedTransaction.builder()
                .id(transaction.getId())
                .customerId(transaction.getCustomerId())
                .accountId(transaction.getAccountId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .type(transaction.getType())
                .channel(transaction.getChannel())
                .beneficiaryId(transaction.getBeneficiaryId())
                .beneficiaryName(transaction.getBeneficiaryName())
                .beneficiaryCountry(transaction.getBeneficiaryCountry())
                .ipAddress(transaction.getIpAddress())
                .location(transaction.getLocation())
                .deviceId(transaction.getDeviceId())
                .timestamp(transaction.getTimestamp())
                .merchantCategory(transaction.getMerchantCategory())
                .metadata(transaction.getMetadata())
                .customerProfile(profile)
                .timeSinceLastTransaction(Duration.ZERO) // TODO: from stream/DB
                .avgAmount30d(profile != null ? profile.getAvgAmount30d() : BigDecimal.ZERO)
                .transactionCount24h(profile != null ? profile.getTransactionCount24h() : 0)
                .build();
    }

    private CustomerProfile resolveProfile(String customerId) {
        // TODO: call customer-service or Redis; stub for now
        return CustomerProfile.builder()
                .customerId(customerId)
                .homeCountry("US")
                .ageDays(365)
                .avgAmount30d(BigDecimal.valueOf(500))
                .transactionCount24h(0)
                .riskScore(BigDecimal.valueOf(0.1))
                .updatedAt(Instant.now())
                .build();
    }
}
