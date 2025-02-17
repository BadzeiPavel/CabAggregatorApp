package com.modsen.auth_service.services;

import com.modsen.auth_service.models.dto.LogoutDTO;
import com.modsen.auth_service.models.dto.AuthUserDTO;
import com.modsen.auth_service.models.entities.User;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IAuthService {
    User register(AuthUserDTO authUserDTO);
    Mono<ResponseEntity<String>> login(AuthUserDTO authUserDTO);
    Mono<ResponseEntity<String>> logout(LogoutDTO logoutDTO);
}
