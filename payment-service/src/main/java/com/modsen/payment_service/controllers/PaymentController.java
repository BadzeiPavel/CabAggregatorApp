package com.modsen.payment_service.controllers;

import com.modsen.payment_service.models.dtos.PaymentDTO;
import com.modsen.payment_service.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import models.dtos.DateRangeDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createPayment(paymentDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPayment(@PathVariable String id) {
        return ResponseEntity.ok(service.getPayment(id));
    }

    @GetMapping("/passengers/{passengerId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByPassengerId(@PathVariable String passengerId) {
        return ResponseEntity.ok(service.getPaymentsByPassengerId(passengerId));
    }

    @GetMapping("/passengers/{passengerId}/date-range")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByPassengerIdInDateRange(@PathVariable String passengerId,
                                                                                @RequestParam @DateTimeFormat(
                                                                                    iso = DateTimeFormat.ISO.DATE_TIME
                                                                                )
                                                                                LocalDateTime timeFrom,
                                                                                @RequestParam @DateTimeFormat(
                                                                                        iso = DateTimeFormat.ISO.DATE_TIME
                                                                                )
                                                                                LocalDateTime timeTo) {
        List<PaymentDTO> payments = service.getPaymentsByPassengerIdInDateRange(passengerId, timeFrom, timeTo);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByDriverId(@PathVariable String driverId) {
        return ResponseEntity.ok(service.getPaymentsByDriverId(driverId));
    }

    @GetMapping("/drivers/{driverId}/date-range")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByDriverIdInDateRange(@PathVariable String passengerId,
                                                                             @RequestParam @DateTimeFormat(
                                                                                     iso = DateTimeFormat.ISO.DATE_TIME
                                                                             )
                                                                             LocalDateTime timeFrom,
                                                                             @RequestParam @DateTimeFormat(
                                                                                     iso = DateTimeFormat.ISO.DATE_TIME
                                                                             )
                                                                             LocalDateTime timeTo) {
        List<PaymentDTO> payments = service.getPaymentsByDriverIdInDateRange(passengerId, timeFrom, timeTo);
        return ResponseEntity.ok(payments);
    }

    @PutMapping("/{id}/completed")
    public ResponseEntity<PaymentDTO> makePaymentOnCompletedRide(@PathVariable String id) {
        PaymentDTO paymentDTO = service.makePaymentOnCompletedRide(id);
        return ResponseEntity.ok(paymentDTO);
    }

}
