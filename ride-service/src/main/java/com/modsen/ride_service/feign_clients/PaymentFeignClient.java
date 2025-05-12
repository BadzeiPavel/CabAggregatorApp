package com.modsen.ride_service.feign_clients;

import jakarta.validation.Valid;
import models.dtos.PaymentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "payment-service")
public interface PaymentFeignClient {

    @PostMapping("/api/v1/payments")
    ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentDTO paymentDTO);

    @DeleteMapping("/api/v1/payments/{rideId}")
    ResponseEntity<PaymentDTO> deletePayment(@PathVariable String rideId);
}
