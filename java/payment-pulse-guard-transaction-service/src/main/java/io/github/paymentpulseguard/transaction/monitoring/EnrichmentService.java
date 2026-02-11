package io.github.paymentpulseguard.transaction.monitoring;

import io.github.paymentpulseguard.domain.EnrichedTransaction;
import io.github.paymentpulseguard.domain.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrichmentService {

    private final WebClient.Builder webClientBuilder;

    @Value("${app.enrichment-service.url:http://127.0.0.1:8085}")
    private String enrichmentServiceUrl;

    public EnrichedTransaction enrich(Transaction transaction) {
        log.debug("Calling remote enrichment service for transaction: {}", transaction.getId());
        return webClientBuilder.build()
                .post()
                .uri(enrichmentServiceUrl + "/enrich")
                .bodyValue(transaction)
                .retrieve()
                .bodyToMono(EnrichedTransaction.class)
                .block(); // Blocking here because the flow is synchronous
    }
}
