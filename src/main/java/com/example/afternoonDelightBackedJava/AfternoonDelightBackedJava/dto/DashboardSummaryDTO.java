package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryDTO {
    private BigDecimal totalBalance;
    private BigDecimal currentMonthCost;
    private BigDecimal currentMonthCredit;
    private List<EmployeeBalanceDTO> negativeBalanceEmployees;
    private List<EmployeeBalanceDTO> highBalanceEmployees;
    private List<EmployeeBalanceDTO> lowBalanceEmployees;
}
