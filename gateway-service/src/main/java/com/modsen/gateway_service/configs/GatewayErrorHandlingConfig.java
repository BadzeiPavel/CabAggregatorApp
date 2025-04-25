package com.modsen.gateway_service.configs;

import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayErrorHandlingConfig {

    @Bean
    public WebExceptionHandler customExceptionHandler() {
        return (ServerWebExchange exchange, Throwable ex) -> {
            if (ex instanceof NotFoundException) {
                exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory()
                        .wrap("Route/Service not found".getBytes()))
                );
            }
            return Mono.error(ex);
        };
    }
}