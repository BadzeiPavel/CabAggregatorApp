package com.modsen.auth_service.controller;

import com.modsen.auth_service.models.dto.AuthUserDTO;
import com.modsen.auth_service.models.dto.LogoutDTO;
import com.modsen.auth_service.models.dto.RegisterRequest;
import com.modsen.auth_service.models.entities.User;
import com.modsen.auth_service.services.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import models.dtos.UserPatchDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Authentication", description = "API for authentication")
@RestController
@RequestMapping("/api/v1/auth/user")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @Operation(summary = "Login user", description = "Login user via keycloak, send access/refresh token")
    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@Valid @RequestBody AuthUserDTO authUserDTO) {
        return service.login(authUserDTO);
    }

    @Operation(
            summary = "Register user",
            description = "Register user in keycloak, register user in passenger/driver service via FeignClient"
    )
    @PostMapping("/register")
    public Mono<ResponseEntity<User>> register(@Valid @RequestBody RegisterRequest request) {
        return Mono.just(ResponseEntity.status(HttpStatus.CREATED)
                .body(service.register(request)));
    }

    @Operation(summary = "Logout user", description = "Logout user by refresh token")
    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logout(@Valid @RequestBody LogoutDTO logoutDTO) {
        return service.logout(logoutDTO);
    }

    @Operation(summary = "Patch user", description = "Patch user by user_id(usage in passenger/driver service)")
    @PutMapping("/{userId}")
    public ResponseEntity<String> patch(@PathVariable String userId, @Valid @RequestBody UserPatchDTO userPatchDTO) {
        return service.updateUser(userId, userPatchDTO);
    }

    @Operation(summary = "Delete user", description = "Delete user by user_id(usage in passenger/driver service)")
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> delete(@PathVariable String userId) {
        return service.deleteUser(userId);
    }
}
