package com.modsen.driver_service.feign_clients;

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
@FeignClient(name = "auth-controller", url = "http://localhost:8081/api/v1/auth/user")
public interface AuthFeignClient {

    @PutMapping("/{userId}")
    ResponseEntity<String> patch(@PathVariable String userId, @Valid @RequestBody UserPatchDTO userPatchDTO);

    @DeleteMapping("/{userId}")
    ResponseEntity<String> delete(@PathVariable String userId);
}
