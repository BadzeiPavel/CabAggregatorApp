package com.modsen.auth_service.controller;

import com.modsen.auth_service.models.dto.AuthUserDTO;
import com.modsen.auth_service.models.dto.LogoutDTO;
import com.modsen.auth_service.models.dto.RegisterRequest;
import com.modsen.auth_service.models.entities.User;
import com.modsen.auth_service.services.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import models.dtos.UserPatchDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth/user")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@Valid @RequestBody AuthUserDTO authUserDTO) {
        return service.login(authUserDTO);
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<User>> register(@Valid @RequestBody RegisterRequest request) {
        return Mono.just(ResponseEntity.status(HttpStatus.CREATED)
                .body(service.register(request)));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logout(@Valid @RequestBody LogoutDTO logoutDTO) {
        return service.logout(logoutDTO);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<String> patch(@PathVariable String userId, @Valid @RequestBody UserPatchDTO userPatchDTO) {
        return service.updateUser(userId, userPatchDTO);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> delete(@PathVariable String userId) {
        return service.deleteUser(userId);
    }
}
