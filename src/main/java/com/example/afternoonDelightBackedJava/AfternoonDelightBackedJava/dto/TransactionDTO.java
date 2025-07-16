package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private Long employeeId;
    private Long mealId;
    private BigDecimal amount;
    private LocalDate transactionDate;
}