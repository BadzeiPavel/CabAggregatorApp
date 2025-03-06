package com.modsen.ride_service.feign_clients;

import models.dtos.requests.ChangeDriverStatusRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "driver-service", url = "http://localhost:8083/api/v1/drivers")
public interface DriverServiceFeignClient {

    @PutMapping("/{id}/status")
    ResponseEntity<Void> changeDriverStatus(@PathVariable("id") UUID id, @RequestBody ChangeDriverStatusRequestDTO requestDTO);
}
