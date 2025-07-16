package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    private Long id;
    private Long employeeId;
    private Long mealId;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
