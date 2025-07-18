package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeMealHistoryReport {
    private Long employeeId;
    private String employeeName;
    private String pin;
    private String period;
    private int totalMeals;
    private BigDecimal totalSpent;
    private BigDecimal averagePerMeal;
    private List<MealHistoryItem> meals;
    private LocalDate startDate;
    private LocalDate endDate;

    // Getters and setters
}
