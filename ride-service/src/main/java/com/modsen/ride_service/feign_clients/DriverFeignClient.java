package com.modsen.ride_service.feign_clients;

import models.dtos.GetFreeDriverNotInListRequest;
import models.dtos.requests.ChangeDriverStatusRequest;
import models.dtos.responses.FreeDriver;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Service
@FeignClient(name = "driver-service")
public interface DriverFeignClient {

    @PutMapping("/api/v1/drivers/{id}/status")
    ResponseEntity<Void> changeDriverStatus(
            @PathVariable("id") UUID id,
            @RequestBody ChangeDriverStatusRequest requestDTO
    );

    @PostMapping("/api/v1/drivers/free")
    ResponseEntity<FreeDriver> getFreeDriverNotInList(
            @RequestBody GetFreeDriverNotInListRequest getFreeDriverNotInListRequest
    );
}
