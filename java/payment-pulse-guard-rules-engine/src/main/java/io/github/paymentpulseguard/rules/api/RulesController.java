package io.github.paymentpulseguard.rules.api;

import io.github.paymentpulseguard.domain.EnrichedTransaction;
import io.github.paymentpulseguard.domain.RuleResult;
import io.github.paymentpulseguard.rules.service.DroolsRulesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/evaluate")
@RequiredArgsConstructor
public class RulesController {
    private final DroolsRulesService droolsRulesService;

    @PostMapping
    public RuleResult evaluate(@RequestBody EnrichedTransaction transaction) {
        return droolsRulesService.evaluate(transaction);
    }
}
