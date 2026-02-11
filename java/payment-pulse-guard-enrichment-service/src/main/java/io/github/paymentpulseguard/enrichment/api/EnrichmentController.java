package io.github.paymentpulseguard.enrichment.api;

import io.github.paymentpulseguard.domain.EnrichedTransaction;
import io.github.paymentpulseguard.domain.Transaction;
import io.github.paymentpulseguard.enrichment.service.EnrichmentLogicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enrich")
@RequiredArgsConstructor
@Slf4j
public class EnrichmentController {

    private final EnrichmentLogicService enrichmentLogicService;

    @PostMapping
    public EnrichedTransaction enrich(@RequestBody Transaction transaction) {
        log.info("Enriching transaction: {}", transaction.getId());
        return enrichmentLogicService.enrich(transaction);
    }
}
