package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodPreferenceStatsDTO {
    private String foodName;
    private Long mealCount;
}