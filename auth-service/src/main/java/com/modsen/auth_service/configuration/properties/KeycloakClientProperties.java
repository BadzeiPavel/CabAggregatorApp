package com.modsen.auth_service.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "app.keycloak.client")
public class KeycloakClientProperties {
    private String id;
    private String secret;
}
