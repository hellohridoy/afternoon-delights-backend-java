package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkMealRequestDTO {
    private List<MealRequestDTO> meals;
}
