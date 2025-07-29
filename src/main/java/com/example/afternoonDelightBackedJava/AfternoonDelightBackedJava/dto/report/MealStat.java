package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class MealStat {
    private int mealCount;
    private int totalParticipants;
    private BigDecimal totalCost;

    public void addMeal(int participants, BigDecimal cost) {
        this.mealCount++;
        this.totalParticipants += participants;
        this.totalCost = this.totalCost == null ? cost : this.totalCost.add(cost);
    }
}
