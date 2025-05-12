package com.modsen.auth_service.feign_clients;

import jakarta.validation.Valid;
import models.dtos.DriverDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "docker-driver-service")
public interface DriverFeignClient {

    @PostMapping("/api/v1/drivers")
    ResponseEntity<DriverDTO> createDriver(@Valid @RequestBody DriverDTO driverDTO);
}