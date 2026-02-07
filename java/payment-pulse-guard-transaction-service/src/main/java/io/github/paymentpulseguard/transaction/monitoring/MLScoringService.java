package io.github.paymentpulseguard.transaction.monitoring;

import io.github.paymentpulseguard.domain.EnrichedTransaction;
import io.github.paymentpulseguard.ml.MLScore;
import io.github.paymentpulseguard.ml.MLTransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.temporal.ChronoField;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MLScoringService {

    private final WebClient.Builder webClientBuilder;

    @Value("${app.ml-service.url:http://localhost:8000}")
    private String mlServiceBaseUrl;

    public CompletableFuture<MLScore> scoreTransactionAsync(EnrichedTransaction transaction) {
        return webClient()
                .post()
                .uri(mlServiceBaseUrl + "/score")
                .bodyValue(toRequest(transaction))
                .retrieve()
                .bodyToMono(MLScore.class)
                .toFuture();
    }

    public MLScore scoreTransaction(EnrichedTransaction transaction) {
        return webClient()
                .post()
                .uri(mlServiceBaseUrl + "/score")
                .bodyValue(toRequest(transaction))
                .retrieve()
                .bodyToMono(MLScore.class)
                .block();
    }

    private WebClient webClient() {
        return webClientBuilder.build();
    }

    private static MLTransactionRequest toRequest(EnrichedTransaction tx) {
        var profile = tx.getCustomerProfile();
        double avg30d = profile != null && profile.getAvgAmount30d() != null
                ? profile.getAvgAmount30d().doubleValue() : 0d;
        int count24h = profile != null ? profile.getTransactionCount24h() : 0;
        double risk = profile != null && profile.getRiskScore() != null
                ? profile.getRiskScore().doubleValue() : 0d;
        int ageDays = profile != null ? profile.getAgeDays() : 0;
        String country = tx.getLocation() != null ? tx.getLocation().getCountry() : "";
        return MLTransactionRequest.builder()
                .id(tx.getId())
                .amount(tx.getAmount() != null ? tx.getAmount().doubleValue() : 0)
                .customerId(tx.getCustomerId())
                .transactionType(tx.getType() != null ? tx.getType().name() : "UNKNOWN")
                .channel(tx.getChannel() != null ? tx.getChannel() : "")
                .beneficiaryCountry(tx.getBeneficiaryCountry() != null ? tx.getBeneficiaryCountry() : "")
                .hourOfDay(tx.getTimestamp() != null ? tx.getTimestamp().get(ChronoField.HOUR_OF_DAY) : 0)
                .dayOfWeek(tx.getTimestamp() != null ? tx.getTimestamp().get(ChronoField.DAY_OF_WEEK) : 0)
                .customerAgeDays(ageDays)
                .avgTransactionAmount30d(avg30d)
                .transactionCount24h(count24h)
                .customerRiskScore(risk)
                .ipCountry(country)
                .deviceFingerprint(tx.getDeviceId() != null ? tx.getDeviceId() : "")
                .build();
    }
}
