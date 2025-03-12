package com.modsen.ride_service.feign_clients;

import jakarta.validation.Valid;
import models.dtos.PaymentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-controller", url = "http://localhost:8086/api/v1/payments")
public interface PaymentFeignClient {

    @PostMapping
    ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentDTO paymentDTO);

    @DeleteMapping("/{rideId}")
    ResponseEntity<PaymentDTO> deletePayment(@PathVariable String rideId);
}
