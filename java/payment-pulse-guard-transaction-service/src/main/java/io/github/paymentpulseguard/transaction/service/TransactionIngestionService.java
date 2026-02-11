package io.github.paymentpulseguard.transaction.service;

import io.github.paymentpulseguard.domain.Transaction;
import io.github.paymentpulseguard.transaction.domain.TransactionEntity;
import io.github.paymentpulseguard.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionIngestionService {

    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    @Value("${app.kafka.topics.transactions:transactions}")
    private String transactionsTopic;

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

        // Save to Database
        try {
            TransactionEntity entity = mapToEntity(transaction);
            transactionRepository.save(entity);
        } catch (Exception e) {
            log.error("Failed to save transaction to DB: {}", transaction.getId(), e);
            // We might want to throw an exception here to fail the request, 
            // or continue if Kafka publishing is more important. 
            // For now, we log and proceed to ensure the pipeline continues.
        }

        // Publish to Kafka
        try {
            kafkaTemplate.send(transactionsTopic, transaction.getId(), transaction);
        } catch (Exception e) {
            log.error("Failed to publish transaction to Kafka: {}", transaction.getId(), e);
            throw new RuntimeException("Failed to publish transaction", e);
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
