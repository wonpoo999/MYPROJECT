// src/main/java/com/example/health_dietcare/entity/FoodLog.java
package com.example.health_dietcare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(indexes = {
    @Index(name="ix_foodlog_user_created", columnList="user_id,createdAt"),
})
public class FoodLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(nullable=false, length=120)
    private String foodName;

    private Double cal;  private Double protein;  private Double carb;  private Double fat;

    private Integer score;           // [ADDED] AI 평가 점수(0~100)
    @Column(length=10)               // [ADDED] AI 라벨(약/중립/독)
    private String label;

    private String imgUrl;

    @Column(nullable=false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
