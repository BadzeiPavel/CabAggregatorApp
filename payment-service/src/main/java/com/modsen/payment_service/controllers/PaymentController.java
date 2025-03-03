package com.modsen.payment_service.controllers;

import com.modsen.payment_service.models.dtos.PaymentDTO;
import com.modsen.payment_service.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import models.dtos.GetAllResponseDTO;
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
    public ResponseEntity<GetAllResponseDTO<PaymentDTO>> getPaymentsByPassengerId(@PathVariable String passengerId) {
        List<PaymentDTO> payments =service.getPaymentsByPassengerId(passengerId);
        GetAllResponseDTO<PaymentDTO> responseDTO = new GetAllResponseDTO<>(payments);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/passengers/{passengerId}/date-range")
    public ResponseEntity<GetAllResponseDTO<PaymentDTO>> getPaymentsByPassengerIdInDateRange(@PathVariable String passengerId,
                                                                                @RequestParam @DateTimeFormat(
                                                                                    iso = DateTimeFormat.ISO.DATE_TIME
                                                                                )
                                                                                LocalDateTime from,
                                                                                @RequestParam @DateTimeFormat(
                                                                                    iso = DateTimeFormat.ISO.DATE_TIME
                                                                                )
                                                                                LocalDateTime to) {
        List<PaymentDTO> payments = service.getPaymentsByPassengerIdInDateRange(passengerId, from, to);
        GetAllResponseDTO<PaymentDTO> responseDTO = new GetAllResponseDTO<>(payments);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<GetAllResponseDTO<PaymentDTO>> getPaymentsByDriverId(@PathVariable String driverId) {
        List<PaymentDTO> payments = service.getPaymentsByDriverId(driverId);
        GetAllResponseDTO<PaymentDTO> responseDTO = new GetAllResponseDTO<>(payments);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/drivers/{driverId}/date-range")
    public ResponseEntity<GetAllResponseDTO<PaymentDTO>> getPaymentsByDriverIdInDateRange(@PathVariable String driverId,
                                                                             @RequestParam @DateTimeFormat(
                                                                                     iso = DateTimeFormat.ISO.DATE_TIME
                                                                             )
                                                                             LocalDateTime from,
                                                                             @RequestParam @DateTimeFormat(
                                                                                     iso = DateTimeFormat.ISO.DATE_TIME
                                                                             )
                                                                             LocalDateTime to) {
        List<PaymentDTO> payments = service.getPaymentsByDriverIdInDateRange(driverId, from, to);
        GetAllResponseDTO<PaymentDTO> responseDTO = new GetAllResponseDTO<>(payments);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}/completed")
    public ResponseEntity<PaymentDTO> makePaymentOnCompletedRide(@PathVariable String id) {
        PaymentDTO paymentDTO = service.makePaymentOnCompletedRide(id);
        return ResponseEntity.ok(paymentDTO);
    }
}
