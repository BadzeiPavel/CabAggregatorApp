package com.modsen.gateway_service.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<String> authServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Authentication Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/driver")
    public ResponseEntity<String> driverServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Driver Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/passenger")
    public ResponseEntity<String> passengerServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Passenger Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/ride")
    public ResponseEntity<String> rideServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Ride Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/rating")
    public ResponseEntity<String> ratingServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Rating Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/payment")
    public ResponseEntity<String> paymentServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Payment Service is temporarily unavailable. Please try again later.");
    }

    // Generic fallback for any undefined routes
    @GetMapping
    public ResponseEntity<String> genericFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/default")
    public ResponseEntity<String> defaultFallback() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Resource not found");
    }
}