// src/main/java/com/example/health_dietcare/repository/UserSecretQARepository.java
package com.example.health_dietcare.repository;

import com.example.health_dietcare.entity.User;
import com.example.health_dietcare.entity.UserSecretQA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSecretQARepository extends JpaRepository<UserSecretQA, Long> {
    Optional<UserSecretQA> findByUser(User user);
}
