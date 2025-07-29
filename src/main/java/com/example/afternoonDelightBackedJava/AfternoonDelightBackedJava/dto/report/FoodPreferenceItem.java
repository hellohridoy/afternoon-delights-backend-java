package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodPreferenceItem {
    private String foodName;
    private int mealCount;
    private int totalParticipants;
    private double participationRate;
    // Getters and setters
}
