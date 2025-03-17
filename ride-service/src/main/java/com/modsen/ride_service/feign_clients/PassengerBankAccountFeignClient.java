package com.modsen.ride_service.feign_clients;

import models.dtos.PassengerBankAccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passenger-bank-account-controller", url = "http://localhost:8086/api/v1/bank-accounts/passengers")
public interface PassengerBankAccountFeignClient {

    @GetMapping("{id}")
    ResponseEntity<PassengerBankAccountDTO> getBalance(@PathVariable String id);
}
