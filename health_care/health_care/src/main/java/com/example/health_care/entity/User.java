// 파일 위치: src/main/java/com/example/health_care/entity/User.java
package com.example.health_care.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;     // 너희 코드에선 'loginId' 등 다른 이름일 수 있음

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    // ----- 기본값을 빌더에도 반영하려면 @Builder.Default 필수 -----

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private boolean profilePublic = false;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_security_questions", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "question_key")
    @Column(name = "answer")
    private Map<String, String> securityQA = new HashMap<>();

    // 나머지 필드(이름/성별/키/몸무게 등)는 기존대로 두면 됨.
    // 단, = 초기값 이 들어있는 필드는 전부 @Builder.Default 붙여라.
    
    public enum Role { USER, ADMIN }
}
