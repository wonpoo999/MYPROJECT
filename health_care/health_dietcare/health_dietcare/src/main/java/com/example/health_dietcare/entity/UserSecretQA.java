// src/main/java/com/example/health_dietcare/entity/UserSecretQA.java
package com.example.health_dietcare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(indexes = @Index(name="ix_userqa_user", columnList="user_id", unique = true))
public class UserSecretQA {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false, unique=true)
    private User user;

    private String bornTown;
    private String livedTown;
    private String motherName;
    private String dogName;
    private String elementary;
}
