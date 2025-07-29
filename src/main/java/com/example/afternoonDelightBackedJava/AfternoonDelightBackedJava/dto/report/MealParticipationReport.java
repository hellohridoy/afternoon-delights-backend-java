package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealParticipationReport {
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalMeals;
    private int totalParticipants;
    private BigDecimal totalCost;
    private double avgParticipantsPerMeal;
    private BigDecimal avgCostPerMeal;
    private Map<String, MealStat> mealStats;
}