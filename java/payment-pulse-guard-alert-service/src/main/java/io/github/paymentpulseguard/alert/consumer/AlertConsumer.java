package io.github.paymentpulseguard.alert.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.paymentpulseguard.alert.domain.AlertEntity;
import io.github.paymentpulseguard.alert.repository.AlertRepository;
import io.github.paymentpulseguard.domain.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertConsumer {

    private final AlertRepository alertRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.alerts:alerts}", groupId = "alert-service-group")
    public void processAlert(Alert alert) {
        log.info("Received alert: {}", alert.getId());
        try {
            String violatedRulesJson = objectMapper.writeValueAsString(alert.getViolatedRules());
            String mlFeaturesJson = objectMapper.writeValueAsString(alert.getFeatures());

            AlertEntity entity = AlertEntity.builder()
                    .id(UUID.fromString(alert.getId()))
                    .transactionId(alert.getTransactionId() != null ? UUID.fromString(alert.getTransactionId()) : null)
                    .customerId(alert.getCustomerId())
                    .alertType(alert.getAlertType())
                    .severity(alert.getSeverity() != null ? alert.getSeverity().name() : "UNKNOWN")
                    .riskScore(BigDecimal.valueOf(alert.getRiskScore()))
                    .violatedRules(violatedRulesJson)
                    .mlFeatures(mlFeaturesJson)
                    .status(alert.getStatus() != null ? alert.getStatus().name() : "NEW")
                    .assignedTo(alert.getAssignedTo())
                    .createdAt(alert.getTimestamp() != null ? alert.getTimestamp() : Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            alertRepository.save(entity);
            log.info("Alert saved to DB: {}", alert.getId());

        } catch (Exception e) {
            log.error("Error saving alert to DB: {}", alert.getId(), e);
        }
    }
}
