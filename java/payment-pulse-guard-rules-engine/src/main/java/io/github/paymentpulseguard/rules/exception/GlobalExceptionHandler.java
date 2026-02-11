package io.github.paymentpulseguard.rules.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        // Log the full stack trace for internal debugging
        log.error("An unexpected error occurred: ", e);

        // Return a generic, safe error message to the client
        Map<String, String> errorResponse = Map.of(
            "error", "Internal Server Error",
            "message", "An unexpected error occurred during rule evaluation."
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
