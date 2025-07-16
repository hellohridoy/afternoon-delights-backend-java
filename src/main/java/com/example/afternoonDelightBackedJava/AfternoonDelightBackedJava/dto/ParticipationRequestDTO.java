package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDTO {
    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Meal ID is required")
    private Long mealId;

    @NotNull(message = "Participation status is required")
    private Boolean participated;
}
