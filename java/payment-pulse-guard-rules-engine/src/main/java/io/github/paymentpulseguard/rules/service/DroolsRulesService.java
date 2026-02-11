package io.github.paymentpulseguard.rules.service;

import io.github.paymentpulseguard.domain.EnrichedTransaction;
import io.github.paymentpulseguard.domain.RuleResult;
import lombok.RequiredArgsConstructor;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DroolsRulesService {

    private final KieContainer kieContainer;

    public RuleResult evaluate(EnrichedTransaction transaction) {
        KieSession kieSession = null;
        RuleResult result = new RuleResult();
        try {
            kieSession = kieContainer.newKieSession("rulesKSession");
            kieSession.setGlobal("result", result);
            kieSession.insert(transaction);
            kieSession.fireAllRules();
        } finally {
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
        return result;
    }
}
