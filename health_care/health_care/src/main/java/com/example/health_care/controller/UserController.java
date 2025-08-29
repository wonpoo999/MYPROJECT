package com.example.health_care.controller;

import com.example.health_care.dto.UserDtos;
import com.example.health_care.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService users;

    @GetMapping("/me")
    public UserDtos.ProfileRes me(Authentication auth){
        return users.me(auth);
    }

    @PatchMapping("/privacy")
    public void setPrivacy(Authentication auth, @RequestBody UserDtos.PrivacyReq r){
        users.setPrivacy(auth, r);
    }
}
