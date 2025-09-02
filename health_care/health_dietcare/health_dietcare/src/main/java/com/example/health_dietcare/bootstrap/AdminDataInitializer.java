package com.example.health_dietcare.bootstrap;

import com.example.health_dietcare.entity.User;
import com.example.health_dietcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminDataInitializer {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Bean
    public ApplicationRunner seedAdmin() {
        return args -> {
            if (!users.existsUsernameIgnoreCase("ADMIN")) {
                User admin = User.builder()
                        .username("ADMIN")
                        .email("admin@local")
                        .name("Administrator")
                        .password(encoder.encode("ADMIN"))
                        .role(User.Role.ADMIN)
                        .gender(User.Gender.MALE)
                        .publicProfile(false)
                        .build();
                users.save(admin);
            }
        };
    }
}
