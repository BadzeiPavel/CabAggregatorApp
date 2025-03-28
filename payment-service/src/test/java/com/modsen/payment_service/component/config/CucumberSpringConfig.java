package com.modsen.payment_service.component.config;

import com.modsen.payment_service.PaymentServiceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes =
        PaymentServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "eureka.client.enabled=false",
                "eureka.client.register-with-eureka=false",
                "eureka.client.fetch-registry=false"
        }
)
public class CucumberSpringConfig {
}