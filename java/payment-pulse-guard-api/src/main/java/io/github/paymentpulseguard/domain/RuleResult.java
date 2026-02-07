package io.github.paymentpulseguard.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RuleResult {
    private final List<Violation> violations = new ArrayList<>();

    public void addViolation(String ruleCode, String message) {
        violations.add(new Violation(ruleCode, message));
    }

    public boolean hasViolations() {
        return !violations.isEmpty();
    }

    public boolean hasViolation(String ruleCode) {
        return violations.stream().anyMatch(v -> ruleCode.equals(v.getRuleCode()));
    }

    public record Violation(String ruleCode, String message) {}
}
