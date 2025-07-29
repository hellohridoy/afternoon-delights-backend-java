package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.emums.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyMealPlanDTO {
    private DayOfWeek day;
    private FoodItemDTO defaultFood;
    private boolean active;
    // Getters and setters
}

