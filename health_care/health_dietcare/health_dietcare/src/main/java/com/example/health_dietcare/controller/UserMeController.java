package com.example.health_dietcare.controller;

import com.example.health_dietcare.entity.User;
import com.example.health_dietcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserMeController {

    private final UserRepository users;

    // ✅ 기존 /api/users/me 와 충돌하므로 /api/me 로 단일화
    @GetMapping("/api/me")
    public Map<String,Object> me(Authentication auth){
        Map<String,Object> out = new LinkedHashMap<>();
        if (auth == null || auth.getName() == null) {
            out.put("authenticated", false);
            return out;
        }
        User u = users.findByUsername(auth.getName()).orElse(null);
        out.put("authenticated", true);
        if (u != null) {
            out.put("id", u.getId());
            out.put("username", u.getUsername());
            out.put("name", u.getName());
            out.put("role", u.getRole().name());
            out.put("tier", u.getTier().name());
        } else {
            out.put("username", auth.getName());
        }
        return out;
    }
}
