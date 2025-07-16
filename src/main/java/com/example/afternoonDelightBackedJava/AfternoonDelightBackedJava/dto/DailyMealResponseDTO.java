package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMealResponseDTO {
    private LocalDate date;
    private String status;
    private String message;
    private Long mealId;

    // Getters and setters
}
