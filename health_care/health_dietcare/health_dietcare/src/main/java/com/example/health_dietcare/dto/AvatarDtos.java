package com.example.health_dietcare.dto;

import lombok.*;

public class AvatarDtos {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class CreateReq {
        private String nickname;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AvRes {
        private String nickname;
        private int level;
        private int hp;
        private int atk;
        private int def;
        private int exp;
    }

    // 호환용 (예전 ViewRes 참조 코드가 있어도 깨지지 않게 유지)
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ViewRes {
        private String nickname;
        private int level;
        private int hp;
        private int atk;
        private int def;
        private int exp;

        public AvRes toAvRes() {
            return new AvRes(nickname, level, hp, atk, def, exp);
        }

        public static ViewRes from(AvRes a) {
            return new ViewRes(a.getNickname(), a.getLevel(), a.getHp(), a.getAtk(), a.getDef(), a.getExp());
        }
    }

    // 엔티티 → DTO
    public static AvRes of(com.example.health_dietcare.entity.Avatar a) {
        return new AvRes(a.getNickname(), a.getLevel(), a.getHp(), a.getAtk(), a.getDef(), a.getExp());
    }
}
