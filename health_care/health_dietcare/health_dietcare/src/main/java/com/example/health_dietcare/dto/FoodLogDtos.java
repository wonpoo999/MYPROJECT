// src/main/java/com/example/health_dietcare/dto/FoodLogDtos.java
package com.example.health_dietcare.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

public class FoodLogDtos {
    @Data @AllArgsConstructor
    public static class Item {
        private Long id; private String foodName;
        private Double cal, protein, carb, fat;
        private Integer score; private String label;
        private String imgUrl; private LocalDateTime createdAt;
    }
    @Data @AllArgsConstructor
    public static class MyListRes { private List<Item> items; }
}
