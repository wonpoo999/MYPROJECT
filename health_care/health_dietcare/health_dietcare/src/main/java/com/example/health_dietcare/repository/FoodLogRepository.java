// src/main/java/com/example/health_dietcare/repository/FoodLogRepository.java
package com.example.health_dietcare.repository;

import com.example.health_dietcare.entity.FoodLog;
import com.example.health_dietcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface FoodLogRepository extends JpaRepository<FoodLog, Long> {
    List<FoodLog> findByUserOrderByCreatedAtDesc(User user);

    // 주간 랭킹용: 기간 내 사용자별 점수 합계
    @Query("""
      select fl.user.id, coalesce(sum(fl.score),0)
      from FoodLog fl
      where fl.createdAt between :from and :to
      group by fl.user.id
      order by sum(fl.score) desc
    """)
    List<Object[]> sumScoresByUserInRange(LocalDateTime from, LocalDateTime to);
}
