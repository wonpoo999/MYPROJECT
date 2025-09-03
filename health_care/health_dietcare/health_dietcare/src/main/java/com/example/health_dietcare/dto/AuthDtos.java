package com.example.health_dietcare.dto;

import com.example.health_dietcare.entity.User.Gender;
import lombok.*;

public class AuthDtos {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class LoginReq {
        private String username;
        private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class LoginRes {
        private String token;
        private Long uid;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RegisterReq {
        private String username;
        private String email;
        private String emailCheck;
        private String password;
        private String passwordCheck;

        private String name;
        private Gender gender;
        private Integer heightCm;
        private Double  weightKg;

        /** ✅ 추가 */
        private Integer age;

        private Boolean publicProfile;

        private String bornTown;
        private String livedTown;
        private String motherName;
        private String dogName;
        private String elementary;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ExistsRes {
        private boolean exists;
    }
}
