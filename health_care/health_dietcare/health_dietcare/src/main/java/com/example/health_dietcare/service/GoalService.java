package com.example.health_dietcare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.health_dietcare.dto.GoalDtos;
import com.example.health_dietcare.entity.Goal;
import com.example.health_dietcare.entity.User;
import com.example.health_dietcare.repository.GoalRepository;
import com.example.health_dietcare.repository.UserRepository;

@Service @RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goals; private final UserRepository users;

    public void setGoal(Authentication auth, GoalDtos.SetReq r){
        User u = users.findByUsername(auth.getName()).orElseThrow();
        goals.findByUserAndActive(u, true).forEach(g->{ g.setActive(false); goals.save(g); });
        goals.save(Goal.builder()
                .user(u)
                .dailyActivityTarget(r.getDailyActivityTarget())
                .dailyCalorieTarget(r.getDailyCalorieTarget())
                .active(true).build());
    }
}
