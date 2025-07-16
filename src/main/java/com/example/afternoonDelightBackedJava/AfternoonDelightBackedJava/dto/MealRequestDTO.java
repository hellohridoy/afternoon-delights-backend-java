package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealRequestDTO {
    @NotNull(message = "Meal date is required")
    private String foodItemName;
    @NotNull(message = "Meal date is required")
    private LocalDate mealDate;

    @NotNull(message = "Food item ID is required")
    private BigDecimal totalCost;

    @NotNull(message = "Per head cost is required")
    @Positive(message = "Per head cost must be positive")
    private List<String> participants; // List of employee PINs
    
}
