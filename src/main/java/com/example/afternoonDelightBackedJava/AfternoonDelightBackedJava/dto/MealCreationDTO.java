package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.FoodItem;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

// MealCreationDTO.java
@Data
public class MealCreationDTO {
    private String foodItem;
    private LocalDate mealDate;
    private double totalCost;
    private List<String> participants;
    private String organizerPin;  // Add this field
}
