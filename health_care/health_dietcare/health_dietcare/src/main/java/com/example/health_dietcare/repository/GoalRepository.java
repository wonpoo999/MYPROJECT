package com.example.health_dietcare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.health_dietcare.entity.Goal;
import com.example.health_dietcare.entity.User;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserAndActive(User user, boolean active);
}
