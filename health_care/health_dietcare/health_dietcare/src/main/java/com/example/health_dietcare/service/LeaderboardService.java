package com.example.health_dietcare.service;

import com.example.health_dietcare.dto.LeaderboardDtos;
import com.example.health_dietcare.entity.User;
import com.example.health_dietcare.repository.FoodLogRepository;
import com.example.health_dietcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class LeaderboardService {
    private final FoodLogRepository logs;
    private final UserRepository users;

    public LeaderboardDtos.BoardRes weekly(Authentication auth){
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);
        var from = monday.atStartOfDay();
        var to   = sunday.atTime(23,59,59);

        List<Object[]> rows = logs.sumScoresByUserInRange(from, to);

        Map<Long,Integer> totals = new HashMap<>();
        for (Object[] r : rows) totals.put(((Number)r[0]).longValue(), ((Number)r[1]).intValue());

        List<Map.Entry<Long,Integer>> sorted = totals.entrySet().stream()
                .sorted(Map.Entry.<Long,Integer>comparingByValue().reversed())
                .toList();

        Map<Long,String> uname = users.findAllById(totals.keySet()).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        List<LeaderboardDtos.Entry> top = new ArrayList<>();
        int rank = 1;
        for (var e : sorted){
            top.add(new LeaderboardDtos.Entry(
                    e.getKey(),
                    uname.getOrDefault(e.getKey(), "user"+e.getKey()),
                    e.getValue(),
                    rank++
            ));
        }

        LeaderboardDtos.Entry me = null;
        if (auth != null){
            Long myId = users.findByUsername(auth.getName()).map(User::getId).orElse(null);
            if (myId != null){
                me = top.stream().filter(t -> t.getUserId().equals(myId)).findFirst()
                        .orElse(new LeaderboardDtos.Entry(myId, auth.getName(), 0, top.size()+1));
            }
        }

        int week = today.get(WeekFields.ISO.weekOfWeekBasedYear());
        int weekYear = today.get(IsoFields.WEEK_BASED_YEAR);
        String cw = String.format("%d-W%02d", weekYear, week);

        return new LeaderboardDtos.BoardRes(top.stream().limit(20).toList(), me, cw);
    }
}
