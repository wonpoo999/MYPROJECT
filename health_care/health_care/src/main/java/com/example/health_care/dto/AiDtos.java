package com.example.health_care.dto;

import lombok.*;
import java.util.List;

public class AiDtos {
    // === 요약/할일 ===
    @Data @NoArgsConstructor @AllArgsConstructor public static class SummaryReq { private String text; }
    @Data @AllArgsConstructor public static class SummaryRes { private String summary; }

    @Data @NoArgsConstructor @AllArgsConstructor public static class TodoReq { private String text; }
    @Data @AllArgsConstructor public static class TodoRes { private List<String> todos; }

    // === 음식 평가(게임 스탯 연동) ===
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class FoodEvalReq {
        private String foodName;
        private double cal, protein, carb, fat;
        private Integer dailyCalTarget, dailyProteinTarget, sugarLimit;
    }
    @Data @AllArgsConstructor public static class StatDelta { private int hp, atk, def; }
    @Data @AllArgsConstructor public static class FoodEvalRes {
        private String label;   // 약 | 중립 | 독
        private int score;      // 0~100
        private String comment;
        private StatDelta delta;
    }
}
