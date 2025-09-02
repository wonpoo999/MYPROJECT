// src/main/java/com/example/health_dietcare/dto/LeaderboardDtos.java
package com.example.health_dietcare.dto;

import lombok.*;
import java.util.List;

public class LeaderboardDtos {
    @Data @AllArgsConstructor
    public static class Entry {
        private Long userId; private String username; private int score; private int rank;
    }
    @Data @AllArgsConstructor
    public static class BoardRes {
        private List<Entry> top; private Entry me;
        private String week; // "2025-W36" 같은 표기
    }
}
