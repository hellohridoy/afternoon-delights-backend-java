package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProfileDTO {
    private EmployeeDTO employee;
    private BigDecimal currentBalance;
    private List<MealParticipationDTO> mealHistory;
    private List<TransactionResponseDTO> transactionHistory;
}

