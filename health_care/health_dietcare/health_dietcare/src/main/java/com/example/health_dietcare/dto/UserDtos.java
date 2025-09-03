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
        private User.Gender gender;
        private Integer heightCm;
        private Double weightKg;

        /** ✅ 추가 */
        private Integer age;

        private Boolean publicProfile;
        private User.Tier tier;
        private User.Role role;
    }

    @Data
    public static class PrivacyReq {
        private Boolean publicProfile;
    }
}
