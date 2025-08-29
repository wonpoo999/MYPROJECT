package com.example.health_care.controller;

import com.example.health_care.dto.AuthDtos;
import com.example.health_care.repository.UserRepository;
import com.example.health_care.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    private final UserRepository users;

    @GetMapping("/exists")
    public AuthDtos.ExistsRes exists(@RequestParam String username){
        return new AuthDtos.ExistsRes(users.existsByUsername(username));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthDtos.RegisterReq r){
        auth.register(r);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public AuthDtos.LoginRes login(@RequestBody AuthDtos.LoginReq r){
        return auth.login(r);
    }
}
