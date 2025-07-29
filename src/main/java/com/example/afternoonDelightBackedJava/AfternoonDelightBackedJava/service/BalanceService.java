package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeeDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.BalanceBulkUpdateResult;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.BalanceReportDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.BalanceStatisticsDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.BalanceUpdateDTO;

import java.math.BigDecimal;
import java.util.List;

public interface BalanceService {
    // Basic balance operations
    BigDecimal getEmployeeBalance(String pin);
    void updateEmployeeBalance(String pin, BigDecimal amount);
    void setEmployeeBalance(String pin, BigDecimal newBalance);

    // Range queries
    List<EmployeeDTO> getEmployeesWithBalanceLessThan(BigDecimal threshold);
    List<EmployeeDTO> getEmployeesWithBalanceLessThanEqual(BigDecimal maxBalance);
    List<EmployeeDTO> getEmployeesWithBalanceGreaterThan(BigDecimal threshold);
    List<EmployeeDTO> getEmployeesWithBalanceGreaterThanEqual(BigDecimal minBalance);
    List<EmployeeDTO> getEmployeesWithBalanceBetween(BigDecimal minBalance, BigDecimal maxBalance);
    List<EmployeeDTO> getEmployeesWithExactBalance(BigDecimal exactBalance);

    // Threshold operations
    List<EmployeeDTO> getEmployeesWithLowBalance(BigDecimal threshold);
    List<EmployeeDTO> getEmployeesWithHighBalance(BigDecimal threshold);
    List<EmployeeDTO> getActiveEmployeesInBalanceRange(BigDecimal minBalance, BigDecimal maxBalance);

    // Aggregations and statistics
    BigDecimal getTotalActiveEmployeeBalances();
    BigDecimal getTotalAllEmployeeBalances();
    Long getEmployeesWithNegativeBalanceCount();
    Long getEmployeesWithZeroBalanceCount();
    BalanceStatisticsDTO getBalanceStatistics();

    // Bulk operations
    BalanceBulkUpdateResult updateBalancesForEmployees(List<BalanceUpdateDTO> updates);
    BalanceBulkUpdateResult creditBalancesToEmployees(List<BalanceUpdateDTO> credits);
    BalanceBulkUpdateResult debitBalancesFromEmployees(List<BalanceUpdateDTO> debits);

    // Balance adjustments and validations
    List<EmployeeDTO> getEmployeesRequiringBalanceAdjustment(BigDecimal minimumRequiredBalance);
    void adjustNegativeBalancesToZero();
    void adjustEmployeeBalanceToMinimum(String pin, BigDecimal minimumBalance);

    // Reporting and analytics
    List<BalanceReportDTO> generateBalanceReport();
    List<BalanceReportDTO> generateBalanceReportByRange(BigDecimal minBalance, BigDecimal maxBalance);
    List<BalanceReportDTO> generateNegativeBalanceReport();

    // Balance history and transactions
    BigDecimal calculateTotalDeductions(String pin);
    BigDecimal calculateTotalCredits(String pin);

    // Validation methods
    boolean hasNegativeBalance(String pin);
    boolean hasSufficientBalance(String pin, BigDecimal requiredAmount);
    void validateBalanceOperation(String pin, BigDecimal amount);

    // Administrative operations
    void freezeEmployeeBalance(String pin);
    void unfreezeEmployeeBalance(String pin);
    boolean isBalanceFrozen(String pin);
}
