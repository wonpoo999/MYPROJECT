package com.example.health_care.repository;

import com.example.health_care.entity.Avatar;
import com.example.health_care.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    Optional<Avatar> findByUser(User user);
}
