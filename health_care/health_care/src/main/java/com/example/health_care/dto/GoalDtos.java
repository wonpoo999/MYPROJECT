package com.example.health_care.dto;

import lombok.*;

public class GoalDtos {
    @Data public static class SetReq { private int dailyActivityTarget; private int dailyCalorieTarget; }
}
