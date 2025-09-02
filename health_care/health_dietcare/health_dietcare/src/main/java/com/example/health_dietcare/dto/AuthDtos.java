package com.example.health_dietcare.dto;

import com.example.health_dietcare.entity.Gender;

import lombok.*;

public class AuthDtos {
    @Data public static class RegisterReq {
        private String username; private String password; private String passwordCheck;
        private String email; private String emailCheck;
        private String name; private Gender gender; private Integer heightCm; private Double weightKg;
        // 보안질문
        private String bornTown; private String livedTown; private String motherName; private String dogName; private String elementary;
        // 개인정보 공개
        private Boolean publicProfile;
    }
    @Data public static class LoginReq { private String username; private String password; }
    @Data @AllArgsConstructor public static class LoginRes { private String token; private Long uid; }
    @Data @AllArgsConstructor public static class ExistsRes { private boolean exists; }
}
