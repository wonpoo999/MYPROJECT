package com.example.health_dietcare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Goal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id")
    private User user;

    private int dailyActivityTarget; // 분
    private int dailyCalorieTarget;  // kcal
@Builder.Default private boolean active = true;

}
