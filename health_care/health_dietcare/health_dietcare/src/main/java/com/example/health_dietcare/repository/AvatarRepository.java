package com.example.health_dietcare.repository;

import com.example.health_dietcare.entity.Avatar;
import com.example.health_dietcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    Optional<Avatar> findByUserId(Long userId);
    Optional<Avatar> findByUser(User user);
    boolean existsByUserId(Long userId);
}
