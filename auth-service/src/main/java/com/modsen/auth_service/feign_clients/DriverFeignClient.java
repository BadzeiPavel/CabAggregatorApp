package com.modsen.auth_service.feign_clients;

import jakarta.validation.Valid;
import models.dtos.DriverDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "driver-controller", url = "http://localhost:8083/api/v1/drivers")
public interface DriverFeignClient {

    @PostMapping
    ResponseEntity<DriverDTO> createDriver(@Valid @RequestBody DriverDTO driverDTO);
}