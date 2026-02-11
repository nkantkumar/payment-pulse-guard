package io.github.paymentpulseguard.transaction.monitoring;

import io.github.paymentpulseguard.domain.EnrichedTransaction;
import io.github.paymentpulseguard.domain.RuleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
        return webClientBuilder.build()
                .post()
                .uri(rulesEngineServiceUrl + "/evaluate")
                .bodyValue(transaction)
                .retrieve()
                .bodyToMono(RuleResult.class)
                .block(); // Blocking here because the flow is synchronous
    }
}
