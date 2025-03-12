package com.modsen.auth_service.feign_clients;

import jakarta.validation.Valid;
import models.dtos.PassengerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "passenger-controller", url = "http://localhost:8082/api/v1/passengers")
public interface PassengerFeignClient {

    @PostMapping
    ResponseEntity<PassengerDTO> createPassenger(@Valid @RequestBody PassengerDTO passengerDTO);
}
