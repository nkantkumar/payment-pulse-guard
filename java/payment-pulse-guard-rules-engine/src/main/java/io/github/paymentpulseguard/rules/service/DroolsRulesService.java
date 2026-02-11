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
        KieSession kieSession = kieContainer.newKieSession();
        RuleResult result = new RuleResult();
        kieSession.setGlobal("result", result);
        kieSession.insert(transaction);
        kieSession.fireAllRules();
        kieSession.dispose();
        return result;
    }
}
