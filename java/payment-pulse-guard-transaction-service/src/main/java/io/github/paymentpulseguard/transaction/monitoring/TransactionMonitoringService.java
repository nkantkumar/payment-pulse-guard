package io.github.paymentpulseguard.transaction.monitoring;

import io.github.paymentpulseguard.domain.Alert;
import io.github.paymentpulseguard.domain.EnrichedTransaction;
import io.github.paymentpulseguard.domain.RuleResult;
import io.github.paymentpulseguard.domain.Transaction;
import io.github.paymentpulseguard.ml.MLScore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionMonitoringService {

    private final RulesEngineService rulesEngine;
    private final MLScoringService mlScoringService;
    private final AlertService alertService;
    private final EnrichmentService enrichmentService;

    private static final double ALERT_THRESHOLD = 0.7;

    @KafkaListener(topics = "${app.kafka.topics.transactions:transactions}", groupId = "fraud-detection")
    public void processTransaction(Transaction transaction) {
        log.debug("Processing transaction: {}", transaction.getId());
        try {
            EnrichedTransaction enriched = enrichmentService.enrich(transaction);
            CompletableFuture<MLScore> mlScoreFuture = mlScoringService.scoreTransactionAsync(enriched);
            RuleResult ruleResult = rulesEngine.evaluate(enriched);
            MLScore score = mlScoreFuture.join();

            if (shouldGenerateAlert(ruleResult, score)) {
                Alert alert = createAlert(enriched, ruleResult, score);
                alertService.publish(alert);
            }
        } catch (Exception e) {
            log.error("Error processing transaction: {}", transaction.getId(), e);
            // TODO: publish to dead-letter topic
        }
    }

    private boolean shouldGenerateAlert(RuleResult ruleResult, MLScore mlScore) {
        return mlScore.getCombinedScore() > ALERT_THRESHOLD || ruleResult.hasViolations();
    }

    private Alert createAlert(EnrichedTransaction tx, RuleResult ruleResult, MLScore mlScore) {
        List<String> violatedRules = ruleResult.getViolations().stream()
                .map(RuleResult.Violation::ruleCode)
                .collect(Collectors.toList());
        return Alert.builder()
                .transactionId(tx.getId())
                .customerId(tx.getCustomerId())
                .alertType(determineAlertType(ruleResult, mlScore))
                .severity(calculateSeverity(mlScore.getCombinedScore()))
                .riskScore(mlScore.getCombinedScore())
                .violatedRules(violatedRules)
                .features(mlScore.getFeatures() != null
                        ? mlScore.getFeatures().entrySet().stream()
                        .collect(Collectors.toMap(java.util.Map.Entry::getKey, e -> (Object) e.getValue()))
                        : java.util.Map.of())
                .timestamp(Instant.now())
                .status(Alert.AlertStatus.NEW)
                .build();
    }

    private String determineAlertType(RuleResult ruleResult, MLScore mlScore) {
        if (ruleResult.hasViolations()) return "RULE_VIOLATION";
        if (mlScore.getFraudScore() > ALERT_THRESHOLD) return "FRAUD";
        if (mlScore.getAmlScore() > ALERT_THRESHOLD) return "AML";
        return "COMBINED_RISK";
    }

    private Alert.Severity calculateSeverity(double score) {
        if (score >= 0.9) return Alert.Severity.CRITICAL;
        if (score >= 0.7) return Alert.Severity.HIGH;
        if (score >= 0.5) return Alert.Severity.MEDIUM;
        return Alert.Severity.LOW;
    }
}
