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

    // ✅ 관리자 자동 생성
    @org.springframework.context.annotation.Bean
    org.springframework.boot.CommandLineRunner initAdmin(
            com.example.health_dietcare.repository.UserRepository users,
            org.springframework.security.crypto.password.PasswordEncoder encoder
    ) {
        return args -> {
            users.findByUsername("ADMIN").ifPresentOrElse(u -> {}, () -> {
                com.example.health_dietcare.entity.User u =
                        com.example.health_dietcare.entity.User.builder()
                                .username("ADMIN")
                                .email("admin@example.com")
                                .name("Super Admin")
                                .password(encoder.encode("ADMIN"))
                                .role(com.example.health_dietcare.entity.User.Role.ADMIN)
                                .publicProfile(false)
                                .build();
                users.save(u);
            });
        };
    }
}
