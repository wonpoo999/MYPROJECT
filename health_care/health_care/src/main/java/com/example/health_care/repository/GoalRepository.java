package com.example.health_care.repository;

import com.example.health_care.entity.Goal;
import com.example.health_care.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserAndActive(User user, boolean active);
}
