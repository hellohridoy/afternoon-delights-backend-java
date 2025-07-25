package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class MealCreationDTO {
    private String foodItem;  // Food item name or ID
    private LocalDate mealDate;
    private BigDecimal totalCost;  // Changed from double to BigDecimal
    private List<String> participants;  // List of employee PINs
    private String organizerPin;  // PIN of the organizer
    private Boolean isTotalCostFixed = true;  // Default to true

    // Constructor for backward compatibility with double
    public void setTotalCost(double totalCost) {
        this.totalCost = BigDecimal.valueOf(totalCost);
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}