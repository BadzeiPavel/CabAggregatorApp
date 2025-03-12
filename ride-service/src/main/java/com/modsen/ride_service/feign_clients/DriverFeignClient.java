package com.modsen.ride_service.feign_clients;

import models.dtos.GetFreeDriverNotInListRequest;
import models.dtos.requests.ChangeDriverStatusRequest;
import models.dtos.responses.FreeDriver;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "driver-controller", url = "http://localhost:8083/api/v1/drivers")
public interface DriverFeignClient {

    @PutMapping("/{id}/status")
    ResponseEntity<Void> changeDriverStatus(
            @PathVariable("id") UUID id,
            @RequestBody ChangeDriverStatusRequest requestDTO
    );

    @PostMapping("/free")
    ResponseEntity<FreeDriver> getFreeDriverNotInList(
            @RequestBody GetFreeDriverNotInListRequest getFreeDriverNotInListRequest
    );
}
