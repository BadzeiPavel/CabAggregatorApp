package com.modsen.ride_service.feign_clients;

import models.dtos.PassengerBankAccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@FeignClient(name = "payment-service")
public interface PassengerBankAccountFeignClient {

    @GetMapping("/api/v1/bank-accounts/passengers/{id}")
    ResponseEntity<PassengerBankAccountDTO> getBalance(@PathVariable String id);
}
