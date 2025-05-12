package com.modsen.auth_service.configuration;

import com.modsen.auth_service.configuration.properties.KeycloakClientProperties;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
@EnableConfigurationProperties(KeycloakClientProperties.class)
public class ClientConfiguration {

    @Bean
    public Keycloak keycloakClient(@Value("${app.keycloak.url}") String keycloakUrl,
                                   @Value("${app.keycloak.realm}") String realm,
                                   KeycloakClientProperties properties) {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(properties.getId())
                .clientSecret(properties.getSecret())
                .build();
    }

    @Bean
    public HttpMessageConverters messageConverters() {
        return new HttpMessageConverters(new MappingJackson2HttpMessageConverter());
    }
}
