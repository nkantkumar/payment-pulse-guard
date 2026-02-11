package io.github.paymentpulseguard.enrichment.service;

import io.github.paymentpulseguard.domain.CustomerProfile;
import io.github.paymentpulseguard.domain.EnrichedTransaction;
import io.github.paymentpulseguard.domain.Transaction;
import io.github.paymentpulseguard.enrichment.domain.CustomerProfileEntity;
import io.github.paymentpulseguard.enrichment.repository.CustomerProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnrichmentLogicService {

    private final CustomerProfileRepository customerProfileRepository;

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
        log.debug("Fetching profile for customer: {}", customerId);
        Optional<CustomerProfileEntity> entityOpt = customerProfileRepository.findById(customerId);

        if (entityOpt.isEmpty()) {
            log.warn("Customer profile not found for ID: {}. Using default.", customerId);
            return createDefaultProfile(customerId);
        }

        return mapToDomain(entityOpt.get());
    }

    private CustomerProfile mapToDomain(CustomerProfileEntity entity) {
        return CustomerProfile.builder()
                .customerId(entity.getCustomerId())
                .riskScore(entity.getBaseRiskScore())
                .avgAmount30d(entity.getAvgTransactionAmount())
                .transactionCount24h(entity.getTransactionCount30d()) // Assuming 30d count can be used for 24h for now
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private CustomerProfile createDefaultProfile(String customerId) {
        return CustomerProfile.builder()
                .customerId(customerId)
                .homeCountry("US")
                .ageDays(365)
                .avgAmount30d(BigDecimal.valueOf(100))
                .transactionCount24h(0)
                .riskScore(BigDecimal.valueOf(0.1))
                .updatedAt(Instant.now())
                .build();
    }
}
