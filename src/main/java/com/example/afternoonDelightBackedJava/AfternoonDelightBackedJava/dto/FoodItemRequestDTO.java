package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemRequestDTO {
    @NotBlank(message = "Food item name is required")
    private String name;

    private String description;
}
