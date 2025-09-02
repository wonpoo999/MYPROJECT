package com.example.health_dietcare.dto;

import com.example.health_dietcare.entity.Gender;
import com.example.health_dietcare.entity.MembershipTier;

import lombok.*;

public class UserDtos {
    @Data @AllArgsConstructor
    public static class ProfileRes {
        private Long id; private String username; private String email; private String name;
        private Gender gender; private Integer heightCm; private Double weightKg;
        private Boolean publicProfile; private MembershipTier tier;
    }
    @Data public static class PrivacyReq { private Boolean publicProfile; }
}
