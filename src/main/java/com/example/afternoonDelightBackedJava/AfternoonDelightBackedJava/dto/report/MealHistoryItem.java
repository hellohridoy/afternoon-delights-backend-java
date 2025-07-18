package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealHistoryItem {
    private Long mealId;
    private LocalDate date;
    private String foodItemName;
    private BigDecimal cost;

    // Getters and setters
}

