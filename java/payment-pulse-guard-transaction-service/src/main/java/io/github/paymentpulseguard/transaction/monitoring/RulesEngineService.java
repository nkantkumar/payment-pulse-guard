package io.github.paymentpulseguard.transaction.monitoring;

import io.github.paymentpulseguard.domain.EnrichedTransaction;
import io.github.paymentpulseguard.domain.RuleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Evaluates Drools (or pluggable) rules against enriched transactions.
 * Now calls the remote payment-pulse-guard-rules-engine service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RulesEngineService {

    private final WebClient.Builder webClientBuilder;

    @Value("${app.rules-engine.url:http://127.0.0.1:8086}")
    private String rulesEngineServiceUrl;

    public RuleResult evaluate(EnrichedTransaction transaction) {
        log.debug("Calling remote rules engine service for transaction: {}", transaction.getId());
        try {
            ResponseEntity<RuleResult> response = webClientBuilder.build()
                    .post()
                    .uri(rulesEngineServiceUrl + "/evaluate")
                    .bodyValue(transaction)
                    .retrieve()
                    .toEntity(RuleResult.class)
                    .block(); // Blocking here because the flow is synchronous

            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                log.error("Rules engine service returned an error: {}", response != null ? response.getStatusCode() : "Unknown");
                // Return a safe, empty result if the remote service fails
                return new RuleResult();
            }
        } catch (Exception e) {
            log.error("Failed to call rules engine service for transaction: {}", transaction.getId(), e);
            // Return a safe, empty result on communication failure
            return new RuleResult();
        }
    }
}
