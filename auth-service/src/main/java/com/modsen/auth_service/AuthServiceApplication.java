package com.modsen.auth_service;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@EnableFeignClients
public class AuthServiceApplication {

	@PostConstruct
	public void init() {
		Hooks.enableAutomaticContextPropagation(); // Critical for WebFlux
	}

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
