package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for balance statistics and analytics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceStatisticsDTO {
    private BigDecimal totalBalance;
    private BigDecimal averageBalance;
    private BigDecimal minimumBalance;
    private BigDecimal maximumBalance;
    private Long employeesWithNegativeBalance;
    private Long employeesWithZeroBalance;
    private Long totalActiveEmployees;

    // Computed properties
    public BigDecimal getBalanceRange() {
        if (maximumBalance != null && minimumBalance != null) {
            return maximumBalance.subtract(minimumBalance);
        }
        return BigDecimal.ZERO;
    }

    public double getNegativeBalancePercentage() {
        if (totalActiveEmployees == null || totalActiveEmployees == 0) return 0.0;
        return (employeesWithNegativeBalance.doubleValue() / totalActiveEmployees.doubleValue()) * 100.0;
    }

    public double getZeroBalancePercentage() {
        if (totalActiveEmployees == null || totalActiveEmployees == 0) return 0.0;
        return (employeesWithZeroBalance.doubleValue() / totalActiveEmployees.doubleValue()) * 100.0;
    }
}

