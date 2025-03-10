package com.modsen.payment_service.controllers;

import com.modsen.payment_service.models.dtos.PaymentDTO;
import com.modsen.payment_service.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import models.dtos.responses.GetAllPaginatedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
    public ResponseEntity<GetAllPaginatedResponse<PaymentDTO>> getPaginatedPaymentsByPassengerId(
            @PathVariable String passengerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllPaginatedResponse<PaymentDTO> responseDTO =
                service.getPaginatedPaymentsByPassengerId(passengerId, PageRequest.of(page, size));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/passengers/{passengerId}/date-range")
    public ResponseEntity<GetAllPaginatedResponse<PaymentDTO>> getPaginatedPaymentsByPassengerIdInDateRange(
            @PathVariable String passengerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllPaginatedResponse<PaymentDTO> responseDTO =
                service.getPaginatedPaymentsByPassengerIdInDateRange(passengerId, from, to, PageRequest.of(page, size));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<GetAllPaginatedResponse<PaymentDTO>> getPaginatedPaymentsByDriverId(
            @PathVariable String driverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllPaginatedResponse<PaymentDTO> responseDTO =
                service.getPaginatedPaymentsByDriverId(driverId, PageRequest.of(page, size));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/drivers/{driverId}/date-range")
    public ResponseEntity<GetAllPaginatedResponse<PaymentDTO>> getPaginatedPaymentsByDriverIdInDateRange(
            @PathVariable String driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllPaginatedResponse<PaymentDTO> responseDTO =
                service.getPaginatedPaymentsByDriverIdInDateRange(driverId, from, to, PageRequest.of(page, size));
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/rides/{rideId}/completed")
    public ResponseEntity<PaymentDTO> makePaymentOnCompletedRide(@PathVariable String rideId) {
        PaymentDTO paymentDTO = service.makePaymentOnCompletedRide(rideId);
        return ResponseEntity.ok(paymentDTO);
    }
}
