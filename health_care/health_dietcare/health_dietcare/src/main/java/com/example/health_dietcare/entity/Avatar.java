package com.example.health_dietcare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Avatar {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id")
    private User user;

    private String nickname;
@Builder.Default private int level = 1;
@Builder.Default private int hp = 100;
@Builder.Default private int atk = 10;
@Builder.Default private int def = 10;
@Builder.Default private int exp = 0;

}
