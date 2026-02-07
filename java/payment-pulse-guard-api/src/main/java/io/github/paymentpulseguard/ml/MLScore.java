package io.github.paymentpulseguard.ml;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class MLScore {
    private String transactionId;
    private double fraudScore;
    private double amlScore;
    private double combinedScore;
    private Map<String, Double> features;
    private String modelVersion;
    private List<String> explanation;
}
