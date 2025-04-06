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
import models.dtos.UserPatchDTO;
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
import reactor.core.scheduler.Schedulers;
import utils.PatchUtil;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final Keycloak keycloakClient;
    private final WebClient.Builder webClientBuilder;

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
    public Mono<User> register(RegisterRequest request) {
        return Mono.fromCallable(() -> {
                    // Blocking Keycloak operations
                    UserRepresentation user = createUserRepresentation(request);
                    setCredentials(user, request);
                    RealmResource realmResource = keycloakClient.realm(realm);
                    Response response = realmResource.users().create(user);
                    String userId = CreatedResponseUtil.getCreatedId(response);
                    addRole(realmResource, request, userId);
                    return userId;
                })
                .subscribeOn(Schedulers.boundedElastic()) // Offload blocking call
                .flatMap(userId -> sendUserCreationMessage(userId, request)
                        .thenReturn(User.builder()
                                .id(userId)
                                .username(request.getUsername())
                                .email(request.getEmail())
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .build()
                        )
                );
    }

    @Override
    public ResponseEntity<String> updateUser(String userId, UserPatchDTO userPatchDTO) {
        RealmResource realmResource = keycloakClient.realm(realm);
        UserResource userResource = realmResource.users().get(userId);

        UserRepresentation userRepresentation = userResource.toRepresentation();

        PatchUtil.patchIfNotNull(userPatchDTO.getUsername(), userRepresentation::setUsername);
        PatchUtil.patchIfNotNull(userPatchDTO.getEmail(), userRepresentation::setEmail);
        PatchUtil.patchIfNotNull(userPatchDTO.getFirstName(), userRepresentation::setFirstName);
        PatchUtil.patchIfNotNull(userPatchDTO.getLastName(), userRepresentation::setLastName);

        userResource.update(userRepresentation);

        return ResponseEntity.ok("User updated successfully");
    }

    @Override
    public ResponseEntity<String> deleteUser(String userId) {
        RealmResource realmResource = keycloakClient.realm(realm);
        UserResource userResource = realmResource.users().get(userId);

        userResource.remove();

        return ResponseEntity.ok("User deleted successfully");
    }

    private Mono<Void> sendUserCreationMessage(String userId, RegisterRequest request) {
        String servicePath = request.getRole().equalsIgnoreCase("PASSENGER")
                ? "http://passenger-service:8082/api/v1/passengers"
                : "http://driver-service:8083/api/v1/drivers";

        Object dto = request.getRole().equalsIgnoreCase("PASSENGER")
                ? PassengerDTO.builder()
                .id(UUID.fromString(userId))
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .build()
                : DriverDTO.builder()
                .id(UUID.fromString(userId))
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .build();

        return webClientBuilder.build()
                .post()
                .uri(servicePath)
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .then();
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
