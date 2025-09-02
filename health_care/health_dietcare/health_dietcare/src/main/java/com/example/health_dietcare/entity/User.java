package com.example.health_dietcare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "ux_users_username", columnList = "username", unique = true),
        @Index(name = "ux_users_email",    columnList = "email",    unique = true)
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password") // 비밀번호 로그 노출 방지
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 로그인 아이디 */
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    /** 이메일 */
    @Column(nullable = false, unique = true, length = 190)
    private String email;

    /** BCrypt 등으로 해시된 비밀번호 */
    @Column(nullable = false, length = 255)
    private String password;

    /** 표시명 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 권한 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    /** 성별 */
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default
    private Gender gender = Gender.OTHER;

    private Integer heightCm;
    private Double  weightKg;

    @Column(nullable = false)
    @Builder.Default
    private boolean publicProfile = true; // 프론트 기본값과 맞춤

    /** 요금제/등급 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private Tier tier = Tier.FREE;

    public enum Role   { USER, ADMIN }
    public enum Gender { MALE, FEMALE, OTHER }
    public enum Tier   { FREE, PRO, PREMIUM }

    /** JPA가 직접 persist 할 때도 기본값 보장 */
    @PrePersist
    public void prePersistDefaults() {
        if (role == null)   role = Role.USER;
        if (gender == null) gender = Gender.OTHER;
        if (tier == null)   tier = Tier.FREE;
        // publicProfile은 primitive 이므로 이미 false/true 중 하나. 기본 true 유지 목적이면 그대로 둠.
    }
}
