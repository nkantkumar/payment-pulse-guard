package io.github.paymentpulseguard.transaction.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.paymentpulseguard.domain.Transaction;
import io.github.paymentpulseguard.transaction.domain.OutboxEvent;
import io.github.paymentpulseguard.transaction.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Transaction> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.transactions:transactions}")
    private String transactionsTopic;

    @Scheduled(fixedDelay = 1000) // Poll every 1 second
    @Transactional
    public void publishEvents() {
        List<OutboxEvent> events = outboxEventRepository.findUnprocessedEvents();

        for (OutboxEvent event : events) {
            try {
                log.info("Publishing outbox event: {}", event.getId());
                
                // Deserialize payload
                Transaction transaction = objectMapper.readValue(event.getPayload(), Transaction.class);
                
                // Publish to Kafka
                kafkaTemplate.send(transactionsTopic, transaction.getId(), transaction);
                
                // Mark as processed
                event.setProcessedAt(Instant.now());
                outboxEventRepository.save(event);
                
            } catch (Exception e) {
                log.error("Failed to publish outbox event: {}", event.getId(), e);
                // In a real system, we might want to implement a retry mechanism or dead-letter queue here
            }
        }
    }
}
