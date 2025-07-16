package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetTotalCostRequestDTO {
    @NotNull(message = "Meal ID is required")
    private Long mealId;

    @NotNull(message = "Total cost is required")
    @Positive(message = "Total cost must be positive")
    private BigDecimal totalCost;
}
