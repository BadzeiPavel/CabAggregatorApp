package com.modsen.auth_service.services;

import com.modsen.auth_service.models.dto.AuthUserDTO;
import com.modsen.auth_service.models.dto.LogoutDTO;
import com.modsen.auth_service.models.dto.RegisterRequest;
import com.modsen.auth_service.models.entities.User;
import models.dtos.UserPatchDTO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IAuthService {
    User register(RegisterRequest request);
    Mono<ResponseEntity<String>> login(AuthUserDTO authUserDTO);
    Mono<ResponseEntity<String>> logout(LogoutDTO logoutDTO);
    ResponseEntity<String> updateUser(String userId, UserPatchDTO userPatchDTO);
    ResponseEntity<String> deleteUser(String userId);
}
