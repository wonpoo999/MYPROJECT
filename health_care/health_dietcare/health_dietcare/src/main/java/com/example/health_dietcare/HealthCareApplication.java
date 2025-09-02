// src/main/java/com/example/health_dietcare/HealthCareApplication.java
package com.example.health_dietcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.example.health_dietcare.entity")
@EnableJpaRepositories(basePackages = "com.example.health_dietcare.repository")
public class HealthCareApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthCareApplication.class, args);
    }
}
