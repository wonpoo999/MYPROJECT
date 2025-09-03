package com.example.health_dietcare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "APP_USERS",
    indexes = {
        @Index(name = "UX_APP_USERS_USERNAME", columnList = "username", unique = true),
        @Index(name = "UX_APP_USERS_EMAIL",    columnList = "email",    unique = true)
    }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 190)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    @Builder.Default
    private Gender gender = Gender.FEMALE;

    private Integer heightCm;
    private Double  weightKg;

    /** ✅ 추가 */
    private Integer age;

    @Column(nullable = false)
    @Builder.Default
    private boolean publicProfile = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private Tier tier = Tier.FREE;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Avatar avatar;

    // 선택(보안질문)
    @Column(length = 100) private String bornTown;
    @Column(length = 100) private String livedTown;
    @Column(length = 100) private String motherName;
    @Column(length = 100) private String dogName;
    @Column(length = 100) private String elementary;

    public enum Role   { USER, ADMIN }
    public enum Gender { MALE, FEMALE }
    public enum Tier   { FREE, PRO, PREMIUM }

    @PrePersist
    public void prePersistDefaults() {
        if (role == null)   role = Role.USER;
        if (gender == null) gender = Gender.FEMALE;
        if (tier == null)   tier = Tier.FREE;
    }
}
