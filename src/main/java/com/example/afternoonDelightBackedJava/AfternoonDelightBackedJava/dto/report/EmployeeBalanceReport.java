package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeBalanceReport {
    private LocalDate reportDate;
    private BigDecimal totalSystemBalance;
    private long employeeCount;
    private List<EmployeeBalanceItem> employees;
    // Getters and setters
}

// Similar DTOs for other reports