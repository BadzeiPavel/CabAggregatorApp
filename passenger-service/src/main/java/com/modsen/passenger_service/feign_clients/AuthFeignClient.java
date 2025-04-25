package com.modsen.passenger_service.feign_clients;

import jakarta.validation.Valid;
import models.dtos.UserPatchDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "auth-service")
public interface AuthFeignClient {

    @PutMapping("/api/v1/auth/user/{userId}")
    ResponseEntity<String> patch(@PathVariable String userId, @Valid @RequestBody UserPatchDTO userPatchDTO);

    @DeleteMapping("/api/v1/auth/user/{userId}")
    ResponseEntity<String> delete(@PathVariable String userId);
}
