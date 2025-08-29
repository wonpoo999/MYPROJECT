package com.example.health_care.controller;

import com.example.health_care.dto.GoalDtos;
import com.example.health_care.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
