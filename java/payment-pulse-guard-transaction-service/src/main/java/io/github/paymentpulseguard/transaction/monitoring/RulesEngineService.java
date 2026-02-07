package io.github.paymentpulseguard.transaction.monitoring;

import io.github.paymentpulseguard.domain.EnrichedTransaction;
import io.github.paymentpulseguard.domain.RuleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Evaluates Drools (or pluggable) rules against enriched transactions.
 * For initial scaffold we use a simple in-process evaluator; replace with KieSession when Drools is added.
 */
@Service
@RequiredArgsConstructor
public class RulesEngineService {

    private static final List<String> SANCTIONED = List.of("IR", "KP", "SY", "CU");

    public RuleResult evaluate(EnrichedTransaction transaction) {
        RuleResult result = new RuleResult();
        if (transaction.getAmount() != null && transaction.getAmount().doubleValue() > 10_000) {
            result.addViolation("HIGH_VALUE",
                    "Transaction exceeds $10,000: " + transaction.getAmount());
        }
        if (transaction.getBeneficiaryCountry() != null) {
            String c = transaction.getBeneficiaryCountry().toUpperCase();
            if (SANCTIONED.contains(c)) {
                result.addViolation("SANCTIONS",
                        "Transaction to sanctioned country: " + transaction.getBeneficiaryCountry());
            }
        }
        return result;
    }
}
