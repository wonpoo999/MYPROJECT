package com.example.health_dietcare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "AVATAR",
    uniqueConstraints = {
        @UniqueConstraint(name = "UK_AVATAR_USER", columnNames = {"USER_ID"})
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** USER 은 예약어이므로 조인컬럼은 user_id 로, 참조키는 반드시 명시 */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "USER_ID",
        nullable = false,
        referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_AVATAR_USER")
    )
    private User user;

    @Column(name = "NICKNAME", length = 50)
    private String nickname;

    /** Oracle 예약어 LEVEL 회피 → 컬럼명 LVL */
    @Column(name = "LVL", nullable = false)
    @Builder.Default
    private int level = 1;

    @Column(name = "HP", nullable = false)
    @Builder.Default
    private int hp = 100;

    @Column(name = "ATK", nullable = false)
    @Builder.Default
    private int atk = 10;

    /** DEF는 모호해서 명확히 → 컬럼명 DEFENSE */
    @Column(name = "DEFENSE", nullable = false)
    @Builder.Default
    private int def = 5;

    @Column(name = "EXP", nullable = false)
    @Builder.Default
    private int exp = 0;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "CONTENT_TYPE", length = 100)
    private String contentType;

    /** SIZE 예약 가능성 회피 → 컬럼명 FILE_SIZE */
    @Column(name = "FILE_SIZE")
    private Long size;
}
