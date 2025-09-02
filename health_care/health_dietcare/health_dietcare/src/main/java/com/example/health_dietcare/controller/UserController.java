// src/main/java/com/example/health_dietcare/controller/UserController.java
package com.example.health_dietcare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.health_dietcare.dto.UserDtos;
import com.example.health_dietcare.entity.User;
import com.example.health_dietcare.service.UserService;
import com.example.health_dietcare.repository.UserRepository;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService users;
    private final UserRepository repo; // ✅ 추가

    @GetMapping("/me")
    public UserDtos.ProfileRes me(Authentication auth){
        return users.me(auth);
    }

    @PatchMapping("/privacy")
    public void setPrivacy(Authentication auth, @RequestBody UserDtos.PrivacyReq r){
        users.setPrivacy(auth, r);
    }

    // ✅ 관리자 전용: 모든 유저 전체 보기(비공개/보안질문 포함)
    @GetMapping("/all")
    public List<Map<String,Object>> all() {
        List<User> list = repo.findAll();
        List<Map<String,Object>> out = new ArrayList<>();
        for (User u : list) {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("email", u.getEmail());
            m.put("name", u.getName());
            m.put("role", u.getRole());
            m.put("gender", u.getGender());
            m.put("heightCm", u.getHeightCm());
            m.put("weightKg", u.getWeightKg());
            m.put("publicProfile", u.isPublicProfile());
            m.put("tier", u.getTier());
            // 보안질문
            m.put("bornTown", u.getBornTown());
            m.put("livedTown", u.getLivedTown());
            m.put("motherName", u.getMotherName());
            m.put("dogName", u.getDogName());
            m.put("elementary", u.getElementary());
            out.add(m);
        }
        return out;
    }
}
