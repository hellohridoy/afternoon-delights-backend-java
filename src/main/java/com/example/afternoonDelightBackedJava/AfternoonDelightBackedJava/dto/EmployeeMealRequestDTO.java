package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeMealRequestDTO {
    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private Long foodItemId; // Optional for custom food requests

    @NotNull(message = "Requested date is required")
    private LocalDate requestedDate;

    @NotBlank(message = "Request message is required")
    private String requestMessage;
}
