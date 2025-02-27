package com.modsen.auth_service.controller;

import com.modsen.auth_service.models.dto.AuthUserDTO;
import com.modsen.auth_service.models.dto.LogoutDTO;
import com.modsen.auth_service.models.entities.User;
import com.modsen.auth_service.services.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@Valid @RequestBody AuthUserDTO authUserDTO) {
        return authService.login(authUserDTO);
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<User>> register(@Valid @RequestBody AuthUserDTO authUserDTO) {
        return Mono.just(ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(authUserDTO)));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logout(@Valid @RequestBody LogoutDTO logoutDTO) {
        return authService.logout(logoutDTO);
    }
}
