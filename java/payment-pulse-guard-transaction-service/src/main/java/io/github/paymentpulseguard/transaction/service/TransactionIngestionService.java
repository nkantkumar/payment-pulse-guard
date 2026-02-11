package io.github.paymentpulseguard.transaction.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.paymentpulseguard.domain.Transaction;
import io.github.paymentpulseguard.transaction.domain.OutboxEvent;
import io.github.paymentpulseguard.transaction.domain.TransactionEntity;
import io.github.paymentpulseguard.transaction.repository.OutboxEventRepository;
import io.github.paymentpulseguard.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionIngestionService {

    private final TransactionRepository transactionRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Transaction processTransaction(Transaction transaction) {
        // Ensure ID and timestamp are present
        if (transaction.getId() == null) {
            transaction.setId(UUID.randomUUID().toString());
        }
        if (transaction.getTimestamp() == null) {
            transaction.setTimestamp(Instant.now());
        }

        log.info("Ingesting transaction: {}", transaction.getId());

        try {
            // 1. Save Transaction Entity
            TransactionEntity entity = mapToEntity(transaction);
            transactionRepository.save(entity);

            // 2. Save Outbox Event (in the same transaction)
            String payload = objectMapper.writeValueAsString(transaction);
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType("TRANSACTION")
                    .aggregateId(transaction.getId())
                    .type("TRANSACTION_CREATED")
                    .payload(payload)
                    .createdAt(Instant.now())
                    .build();
            outboxEventRepository.save(outboxEvent);

        } catch (Exception e) {
            log.error("Failed to save transaction and outbox event: {}", transaction.getId(), e);
            throw new RuntimeException("Failed to ingest transaction", e);
        }

        return transaction;
    }

    private TransactionEntity mapToEntity(Transaction transaction) {
        return TransactionEntity.builder()
                .id(UUID.fromString(transaction.getId()))
                .customerId(transaction.getCustomerId())
                .accountId(transaction.getAccountId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .type(transaction.getType() != null ? transaction.getType().name() : null)
                .channel(transaction.getChannel())
                .beneficiaryId(transaction.getBeneficiaryId())
                .beneficiaryCountry(transaction.getBeneficiaryCountry())
                .ipAddress(transaction.getIpAddress())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}
