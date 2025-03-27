package com.modsen.driver_service.component.services;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/java/com/modsen/driver_service/component/services/features",
        glue = {
                "com.modsen.driver_service.component.services.steps",
                "com.modsen.driver_service.component.config"
        },
        plugin = {"pretty", "html:target/cucumber-report.html"}
)
public class CucumberTestRunner {
}
