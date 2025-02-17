package com.modsen.auth_service.services.impl;

import com.modsen.auth_service.models.dto.AuthUserDTO;
import com.modsen.auth_service.models.dto.LogoutDTO;
import com.modsen.auth_service.models.entities.User;
import com.modsen.auth_service.services.IAuthService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
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

@Component
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final Keycloak keycloakClient;

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
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Logout failed: " + e.getMessage())));
    }

    @Override
    public User register(AuthUserDTO authUserDto) {
        RealmResource realmResource = keycloakClient.realm(realm);

        UserRepresentation user = createUserRepresentation(authUserDto);
        setCredentials(user, authUserDto);

        Response response = realmResource.users().create(user);
        String userId = CreatedResponseUtil.getCreatedId(response);

        addRole(realmResource, authUserDto, userId);

        return User.builder()
                .id(userId)
                .username(authUserDto.getUsername())
                .email(authUserDto.getEmail())
                .firstName(authUserDto.getFirstName())
                .lastName(authUserDto.getLastName())
                .build();
    }

    private void addRole(RealmResource realmResource, AuthUserDTO authUserDTO, String userId) {
        UserResource userResource = realmResource.users().get(userId);
        RoleRepresentation role = keycloakClient.realm(realm).roles().get(authUserDTO.getRole()).toRepresentation();

        userResource.roles().realmLevel().add(List.of(role));
    }

    private void setCredentials(UserRepresentation userRepresentation, AuthUserDTO authUserDTO) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(authUserDTO.getPassword());

        userRepresentation.setCredentials(List.of(credential));
        userRepresentation.setRealmRoles(List.of(authUserDTO.getRole()));
        userRepresentation.setFirstName(authUserDTO.getFirstName());
        userRepresentation.setLastName(authUserDTO.getLastName());
    }

    private UserRepresentation createUserRepresentation(AuthUserDTO authUserDTO) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(authUserDTO.getUsername());
        user.setEmail(authUserDTO.getEmail());
        user.setEmailVerified(true);
        user.setEnabled(true);

        return user;
    }

}
