package com.example.health_dietcare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.health_dietcare.dto.GoalDtos;
import com.example.health_dietcare.service.GoalService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/goals")
public class GoalController {
    private final GoalService goals;

    @PostMapping
    public void set(Authentication auth, @RequestBody GoalDtos.SetReq r){
        goals.setGoal(auth, r);
    }
}
