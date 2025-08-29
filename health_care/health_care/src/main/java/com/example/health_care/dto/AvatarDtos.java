package com.example.health_care.dto;

import lombok.*;

public class AvatarDtos {
    @Data public static class CreateReq { private String nickname; }
    @Data @AllArgsConstructor public static class AvRes {
        private String nickname; private int level; private int hp; private int atk; private int def; private int exp;
    }
}
