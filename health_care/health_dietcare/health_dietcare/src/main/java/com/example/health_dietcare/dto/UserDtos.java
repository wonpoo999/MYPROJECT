package com.example.health_dietcare.dto;

import com.example.health_dietcare.entity.User;
import lombok.*;

public class UserDtos {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ProfileRes {
        private Long id;
        private String username;
        private String email;
        private String name;
        private User.Gender gender;     // ← 엔티티의 중첩 enum 사용
        private Integer heightCm;
        private Double weightKg;
        private Boolean publicProfile;
        private User.Tier tier;         // ← 엔티티의 중첩 enum 사용
        private User.Role role;         // ← 프론트 인사말에서 필요
    }

    @Data
    public static class PrivacyReq {
        private Boolean publicProfile;
    }
}
