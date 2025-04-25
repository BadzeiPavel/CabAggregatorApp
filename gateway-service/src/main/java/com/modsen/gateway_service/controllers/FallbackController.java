package com.modsen.gateway_service.controllers;

import models.dtos.responses.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @PostMapping("/{serviceName}")
    public ResponseEntity<ErrorResponse> authServiceFallback(@PathVariable String serviceName) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(
                        UUID.randomUUID(),  // Unique error ID for tracking
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Service is unavailable",
                        serviceName + " service is temporarily unavailable. Please try again later.",
                        LocalDateTime.now()
                ));
    }

    @PostMapping
    public ResponseEntity<ErrorResponse> defaultFallback() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        UUID.randomUUID(),  // Unique error ID for tracking
                        HttpStatus.NOT_FOUND.value(),
                        "Resource not found",
                        "Resource not found. Please try again.",
                        LocalDateTime.now()
                ));
    }
}