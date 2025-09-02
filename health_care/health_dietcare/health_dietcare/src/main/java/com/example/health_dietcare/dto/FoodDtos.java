package com.example.health_dietcare.dto;

import lombok.*; import java.util.List;

public class FoodDtos {
    @Data @AllArgsConstructor public static class Candidate { private String name; private double score; }
    @Data @AllArgsConstructor public static class LogRes {
        private String status; private String foodName;
        private Double cal, protein, carb, fat;
        private String imgUrl;
        private List<Candidate> candidates;
    }
    @Data public static class ConfirmReq { private String foodName; private String imgUrl; }
}
