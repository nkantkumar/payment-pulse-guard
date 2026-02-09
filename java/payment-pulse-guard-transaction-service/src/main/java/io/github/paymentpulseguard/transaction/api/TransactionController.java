package io.github.paymentpulseguard.transaction.api;

import io.github.paymentpulseguard.domain.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    @Value("${app.kafka.topics.transactions:transactions}")
    private String transactionsTopic;

    @PostMapping
    public ResponseEntity<Transaction> submitTransaction(@RequestBody Transaction transaction) {
        // Ensure ID and timestamp are present
        if (transaction.getId() == null) {
            transaction.setId(UUID.randomUUID().toString());
        }
        if (transaction.getTimestamp() == null) {
            transaction.setTimestamp(Instant.now());
        }

        log.info("Received transaction: {}", transaction.getId());
        
        // Publish to Kafka
        kafkaTemplate.send(transactionsTopic, transaction.getId(), transaction);
        
        return ResponseEntity.accepted().body(transaction);
    }
}
