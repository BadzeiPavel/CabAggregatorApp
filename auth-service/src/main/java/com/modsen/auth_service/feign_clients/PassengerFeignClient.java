package com.modsen.auth_service.feign_clients;

import jakarta.validation.Valid;
import models.dtos.PassengerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "docker-passenger-service")
public interface PassengerFeignClient {

    @PostMapping("/api/v1/passengers")
    ResponseEntity<PassengerDTO> createPassenger(@Valid @RequestBody PassengerDTO passengerDTO);
}
