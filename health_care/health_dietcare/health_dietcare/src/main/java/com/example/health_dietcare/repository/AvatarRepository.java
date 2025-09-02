package com.example.health_dietcare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.health_dietcare.entity.Avatar;
import com.example.health_dietcare.entity.User;

import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    Optional<Avatar> findByUser(User user);
}
