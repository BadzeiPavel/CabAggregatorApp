package com.modsen.auth_service.services.impl;

import com.modsen.auth_service.feign_clients.DriverFeignClient;
import com.modsen.auth_service.feign_clients.PassengerFeignClient;
import com.modsen.auth_service.models.dto.AuthUserDTO;
import com.modsen.auth_service.models.dto.LogoutDTO;
import com.modsen.auth_service.models.dto.RegisterRequest;
import com.modsen.auth_service.models.entities.User;
import com.modsen.auth_service.services.IAuthService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import models.dtos.DriverDTO;
import models.dtos.PassengerDTO;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final Keycloak keycloakClient;
    private final DriverFeignClient driverFeignClient;
    private final PassengerFeignClient passengerFeignClient;

    @Value("${app.keycloak.token-url}")
    private String tokenUrl;

    @Value("${app.keycloak.logout-url}")
    private String logoutUrl;

    @Value("${app.keycloak.client.id}")
    private String clientId;

    @Value("${app.keycloak.client.secret}")
    private String clientSecret;

    @Value("${app.keycloak.realm}")
    private String realm;

    @Override
    public Mono<ResponseEntity<String>> login(AuthUserDTO authUserDTO) {
        String formData = "grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s"
                .formatted(clientId, clientSecret, authUserDTO.getUsername(), authUserDTO.getPassword());

        return WebClient.builder().build()
                .post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> ResponseEntity.ok().body(response))
                .retry(3)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid credentials")));
    }

    @Override
    public Mono<ResponseEntity<String>> logout(LogoutDTO logoutDTO) {
        String formData = "client_id=%s&client_secret=%s&refresh_token=%s"
                .formatted(clientId, clientSecret, logoutDTO.getRefreshToken());

        return WebClient.builder().build()
                .post()
                .uri(logoutUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .toBodilessEntity()
                .map(response -> ResponseEntity.ok("Logged out successfully"))
                .retry(3)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Logout failed: " + e.getMessage())));
    }

    @Override
    public User register(RegisterRequest request) {
        UserRepresentation user = createUserRepresentation(request);
        setCredentials(user, request);

        RealmResource realmResource = keycloakClient.realm(realm);

        Response response = realmResource.users().create(user);
        String userId = CreatedResponseUtil.getCreatedId(response);

        addRole(realmResource, request, userId);

        sendUserCreationMessage(userId, request);

        return User.builder()
                .id(userId)
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
    }

    private void sendUserCreationMessage(String userId, RegisterRequest request) {
        if(request.getRole().equals("PASSENGER")) {
            passengerFeignClient.createPassenger(PassengerDTO.builder()
                    .id(UUID.fromString(userId))
                    .username(request.getUsername())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .birthDate(request.getBirthDate())
                    .build());
        } else if(request.getRole().equals("DRIVER")) {
            driverFeignClient.createDriver(DriverDTO.builder()
                    .id(UUID.fromString(userId))
                    .username(request.getUsername())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .birthDate(request.getBirthDate())
                    .build());
        }
    }

    private void addRole(RealmResource realmResource, RegisterRequest request, String userId) {
        UserResource userResource = realmResource.users().get(userId);
        RoleRepresentation role = keycloakClient.realm(realm).roles().get(request.getRole()).toRepresentation();

        userResource.roles().realmLevel().add(List.of(role));
    }

    private void setCredentials(UserRepresentation userRepresentation, RegisterRequest request) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());

        userRepresentation.setCredentials(List.of(credential));
        userRepresentation.setRealmRoles(List.of(request.getRole()));
        userRepresentation.setFirstName(request.getFirstName());
        userRepresentation.setLastName(request.getLastName());
    }

    private UserRepresentation createUserRepresentation(RegisterRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setEmailVerified(true);
        user.setEnabled(true);

        return user;
    }

}
