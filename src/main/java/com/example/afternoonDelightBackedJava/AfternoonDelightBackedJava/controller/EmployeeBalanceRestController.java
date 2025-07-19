package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.controller;


import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.BalanceBulkUpdateResult;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.BalanceReportDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.BalanceStatisticsDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.BulkBalanceUpdateRequestDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for employee balance operations
 */
@RestController
@RequiredArgsConstructor
public class EmployeeBalanceRestController {

    private final BalanceService balanceService;

    /**
     * Get balance statistics for all active employees
     */
    @GetMapping("/api/employees/balance/statistics")
    public ResponseEntity<BalanceStatisticsDTO> getBalanceStatistics() {
        BalanceStatisticsDTO statistics = balanceService.getBalanceStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get employees with balance less than or equal to specified amount
     */
    @GetMapping("/api/employees/balance/less-than-equal/{maxBalance}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithBalanceLessThanEqual(
            @PathVariable BigDecimal maxBalance) {
        List<EmployeeDTO> employees = balanceService.getEmployeesWithBalanceLessThanEqual(maxBalance);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get employees with balance greater than or equal to specified amount
     */
    @GetMapping("/api/employees/balance/greater-than-equal/{minBalance}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithBalanceGreaterThanEqual(
            @PathVariable BigDecimal minBalance) {
        List<EmployeeDTO> employees = balanceService.getEmployeesWithBalanceGreaterThanEqual(minBalance);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get employees with balance between two amounts
     */
    @GetMapping("/api/employees/balance/between")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithBalanceBetween(
            @RequestParam BigDecimal minBalance,
            @RequestParam BigDecimal maxBalance) {
        List<EmployeeDTO> employees = balanceService.getEmployeesWithBalanceBetween(minBalance, maxBalance);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get total balance of all active employees
     */
    @GetMapping("/api/employees/balance/total")
    public ResponseEntity<BigDecimal> getTotalActiveEmployeeBalances() {
        BigDecimal total = balanceService.getTotalActiveEmployeeBalances();
        return ResponseEntity.ok(total);
    }

    /**
     * Get employees with low balance (below threshold)
     */
    @GetMapping("/api/employees/balance/low/{threshold}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithLowBalance(
            @PathVariable BigDecimal threshold) {
        List<EmployeeDTO> employees = balanceService.getEmployeesWithLowBalance(threshold);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get employees with high balance (above threshold)
     */
    @GetMapping("/api/employees/balance/high/{threshold}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithHighBalance(
            @PathVariable BigDecimal threshold) {
        List<EmployeeDTO> employees = balanceService.getEmployeesWithHighBalance(threshold);
        return ResponseEntity.ok(employees);
    }

    /**
     * Update balance for a single employee
     */
    @PutMapping("/api/employees/balance/update/{pin}")
    public ResponseEntity<String> updateEmployeeBalance(
            @PathVariable String pin,
            @RequestParam BigDecimal amount) {
        try {
            balanceService.updateEmployeeBalance(pin, amount);
            return ResponseEntity.ok("Balance updated successfully for employee: " + pin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating balance: " + e.getMessage());
        }
    }

    /**
     * Get current balance for an employee
     */
    @GetMapping("/api/employees/balance/{pin}")
    public ResponseEntity<BigDecimal> getEmployeeBalance(@PathVariable String pin) {
        try {
            BigDecimal balance = balanceService.getEmployeeBalance(pin);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Bulk update balances for multiple employees
     */
    @PostMapping("/api/employees/balance/bulk-update")
    public ResponseEntity<BalanceBulkUpdateResult> bulkUpdateBalances(
            @Valid @RequestBody BulkBalanceUpdateRequestDTO request) {
        BalanceBulkUpdateResult result = balanceService.updateBalancesForEmployees(request.getUpdates());
        return ResponseEntity.ok(result);
    }

    /**
     * Generate balance report for all employees
     */
    @GetMapping("/api/employees/balance/report")
    public ResponseEntity<List<BalanceReportDTO>> generateBalanceReport() {
        List<BalanceReportDTO> report = balanceService.generateBalanceReport();
        return ResponseEntity.ok(report);
    }

    /**
     * Get count of employees with negative balance
     */
    @GetMapping("/api/employees/balance/negative/count")
    public ResponseEntity<Long> getNegativeBalanceCount() {
        Long count = balanceService.getEmployeesWithNegativeBalanceCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Get count of employees with zero balance
     */
    @GetMapping("/api/employees/balance/zero/count")
    public ResponseEntity<Long> getZeroBalanceCount() {
        Long count = balanceService.getEmployeesWithZeroBalanceCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Adjust all negative balances to zero
     */
    @PostMapping("/api/employees/balance/adjust-negative")
    public ResponseEntity<String> adjustNegativeBalancesToZero() {
        try {
            balanceService.adjustNegativeBalancesToZero();
            return ResponseEntity.ok("Negative balances adjusted to zero successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adjusting balances: " + e.getMessage());
        }
    }

    /**
     * Get employees requiring balance adjustment
     */
    @GetMapping("/api/employees/balance/adjustment-required/{minimumBalance}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesRequiringBalanceAdjustment(
            @PathVariable BigDecimal minimumBalance) {
        List<EmployeeDTO> employees = balanceService.getEmployeesRequiringBalanceAdjustment(minimumBalance);
        return ResponseEntity.ok(employees);
    }
}
