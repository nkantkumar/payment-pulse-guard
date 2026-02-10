package io.github.paymentpulseguard.transaction.monitoring;

import io.github.paymentpulseguard.domain.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertService {

    private final KafkaTemplate<String, Alert> kafkaTemplate;

    @org.springframework.beans.factory.annotation.Value("${app.kafka.topics.alerts:alerts}")
    private String alertsTopic;

    public void publish(Alert alert) {
        String id = alert.getId() != null ? alert.getId() : java.util.UUID.randomUUID().toString();
        Alert withId = Alert.builder()
                .id(id)
                .transactionId(alert.getTransactionId())
                .customerId(alert.getCustomerId())
                .alertType(alert.getAlertType())
                .severity(alert.getSeverity())
                .riskScore(alert.getRiskScore())
                .violatedRules(alert.getViolatedRules())
                .features(alert.getFeatures())
                .timestamp(alert.getTimestamp())
                .status(alert.getStatus())
                .assignedTo(alert.getAssignedTo())
                .build();
        kafkaTemplate.send(alertsTopic, withId.getId(), withId);
        log.info("Alert published: {}", withId);
        log.info("Alert published: {} - Severity: {}, Score: {}", withId.getId(), withId.getSeverity(), withId.getRiskScore());
    }
}
