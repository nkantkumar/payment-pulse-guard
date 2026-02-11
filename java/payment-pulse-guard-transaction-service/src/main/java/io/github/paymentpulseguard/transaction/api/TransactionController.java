package io.github.paymentpulseguard.transaction.api;

import io.github.paymentpulseguard.domain.Transaction;
import io.github.paymentpulseguard.transaction.service.TransactionIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionIngestionService transactionIngestionService;

    @PostMapping
    public ResponseEntity<Transaction> submitTransaction(@RequestBody Transaction transaction) {
        log.info("Received transaction request");
        Transaction processedTransaction = transactionIngestionService.processTransaction(transaction);
        return ResponseEntity.accepted().body(processedTransaction);
    }
}
