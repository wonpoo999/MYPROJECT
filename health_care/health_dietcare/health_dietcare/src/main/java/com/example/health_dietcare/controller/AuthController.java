package com.example.health_dietcare.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.health_dietcare.dto.AuthDtos;
import com.example.health_dietcare.repository.UserRepository;
import com.example.health_dietcare.service.AuthService;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;
    private final UserRepository users;

    /** 아이디 중복 확인: 어떤 상황이든 200으로 {exists:boolean} */
    @GetMapping("/exists")
    @Transactional(readOnly = true)
    public AuthDtos.ExistsRes exists(
            @RequestParam(name = "username", required = false, defaultValue = "") String username
    ) {
        final String u = username.trim();
        boolean exists = false;

        if (!u.isEmpty()) {
            try {
                // 리포지토리 메서드와 이름 통일 (대소문자 무시)
                exists = users.existsUsernameIgnoreCase(u);
            } catch (Exception e1) {
                try {
                    // 폴백: 정확 일치
                    exists = users.existsByUsername(u);
                    log.warn("exists IC failed, fallback used for '{}': {}", u, e1.toString());
                } catch (Exception e2) {
                    // 최종 실패여도 에러 응답 없이 false 유지
                    log.warn("exists check failed for '{}': {}", u, e2.toString());
                }
            }
        }
        return new AuthDtos.ExistsRes(exists);
    }

    // GET 가이드(회원가입)
    @GetMapping("/register")
    public Map<String, Object> registerHelp() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", "alice");
        body.put("password", "P@ssw0rd!");
        body.put("passwordCheck", "P@ssw0rd!");
        body.put("email", "alice@example.com");
        body.put("emailCheck", "alice@example.com");
        body.put("name", "Alice");
        body.put("gender", "FEMALE");
        body.put("heightCm", 165);
        body.put("weightKg", 55.5);
        body.put("bornTown", "Seoul");
        body.put("livedTown", "Busan");
        body.put("motherName", "Kim");
        body.put("dogName", "Bori");
        body.put("elementary", "ABC");
        body.put("publicProfile", true);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("hint", "POST /api/auth/register 로 JSON 바디를 보내세요");
        out.put("body.example", body);
        return out;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthDtos.RegisterReq r) {
        auth.register(r);
        return ResponseEntity.ok().build();
    }

    // GET 가이드(로그인)
    @GetMapping("/login")
    public Map<String, Object> loginHelp() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", "alice");
        body.put("password", "P@ssw0rd!");

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("hint", "POST /api/auth/login 로 JSON 바디를 보내세요");
        out.put("body.example", body);
        return out;
    }

    @PostMapping("/login")
    public AuthDtos.LoginRes login(@RequestBody AuthDtos.LoginReq r) {
        return auth.login(r);
    }
}
