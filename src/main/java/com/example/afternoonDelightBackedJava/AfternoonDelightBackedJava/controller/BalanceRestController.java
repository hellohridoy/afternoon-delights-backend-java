package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.controller;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for employee balance operations
 * Uses dedicated BalanceService for separation of concerns
 */
@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
public class BalanceRestController {

    private final BalanceService balanceService;

    /**
     * Get balance statistics for all active employees
     */
    @GetMapping("/statistics")
    public ResponseEntity<BalanceStatisticsDTO> getBalanceStatistics() {
        BalanceStatisticsDTO statistics = balanceService.getBalanceStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get employees with balance less than specified amount (strict)
     */
    @GetMapping("/less-than/{threshold}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithBalanceLessThan(
            @PathVariable BigDecimal threshold) {
        List<EmployeeDTO> employees = balanceService.getEmployeesWithBalanceLessThan(threshold);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get employees with balance less than or equal to specified amount
     */
    @GetMapping("/less-than-equal/{maxBalance}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithBalanceLessThanEqual(
            @PathVariable BigDecimal maxBalance) {
        List<EmployeeDTO> employees = balanceService.getEmployeesWithBalanceLessThanEqual(maxBalance);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get employees with balance greater than specified amount (strict)
     */
    @GetMapping("/greater-than/{threshold}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithBalanceGreaterThan(
            @PathVariable BigDecimal threshold) {
        List<EmployeeDTO> employees = balanceService.getEmployeesWithBalanceGreaterThan(threshold);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get employees with balance greater than or equal to specified amount
     */
    @GetMapping("/greater-than-equal/{minBalance}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithBalanceGreaterThanEqual(
            @PathVariable BigDecimal minBalance) {
        List<EmployeeDTO> employees = balanceService.getEmployeesWithBalanceGreaterThanEqual(minBalance);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get employees with exact balance amount
     */
    @GetMapping("/exact/{balance}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithExactBalance(
            @PathVariable BigDecimal balance) {
        List<EmployeeDTO> employees = balanceService.getEmployeesWithExactBalance(balance);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get employees with balance between two amounts
     */
    @GetMapping("/between")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithBalanceBetween(
            @RequestParam BigDecimal minBalance,
            @RequestParam BigDecimal maxBalance) {
        List<EmployeeDTO> employees = balanceService.getEmployeesWithBalanceBetween(minBalance, maxBalance);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get total balance of all active employees
     */
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalActiveEmployeeBalances() {
        BigDecimal total = balanceService.getTotalActiveEmployeeBalances();
        return ResponseEntity.ok(total);
    }

    /**
     * Get employees with low balance (below threshold)
     */
    @GetMapping("/low/{threshold}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithLowBalance(
            @PathVariable BigDecimal threshold) {
        List<EmployeeDTO> employees = balanceService.getEmployeesWithLowBalance(threshold);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get employees with high balance (above threshold)
     */
    @GetMapping("/high/{threshold}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithHighBalance(
            @PathVariable BigDecimal threshold) {
        List<EmployeeDTO> employees = balanceService.getEmployeesWithHighBalance(threshold);
        return ResponseEntity.ok(employees);
    }

    /**
     * Update balance for a single employee
     */
    @PutMapping("/update/{pin}")
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
     * Set balance for a single employee (absolute value)
     */
    @PutMapping("/set/{pin}")
    public ResponseEntity<String> setEmployeeBalance(
            @PathVariable String pin,
            @RequestParam BigDecimal newBalance) {
        try {
            balanceService.setEmployeeBalance(pin, newBalance);
            return ResponseEntity.ok("Balance set successfully for employee: " + pin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error setting balance: " + e.getMessage());
        }
    }

    /**
     * Get current balance for an employee
     */
    @GetMapping("/{pin}")
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
    @PostMapping("/bulk-update")
    public ResponseEntity<BalanceBulkUpdateResult> bulkUpdateBalances(
            @Valid @RequestBody BulkBalanceUpdateRequestDTO request) {
        BalanceBulkUpdateResult result = balanceService.updateBalancesForEmployees(request.getUpdates());
        return ResponseEntity.ok(result);
    }

    /**
     * Bulk credit balances (ensure positive amounts)
     */
    @PostMapping("/bulk-credit")
    public ResponseEntity<BalanceBulkUpdateResult> bulkCreditBalances(
            @Valid @RequestBody List<BalanceUpdateDTO> credits) {
        BalanceBulkUpdateResult result = balanceService.creditBalancesToEmployees(credits);
        return ResponseEntity.ok(result);
    }

    /**
     * Bulk debit balances (ensure negative amounts)
     */
    @PostMapping("/bulk-debit")
    public ResponseEntity<BalanceBulkUpdateResult> bulkDebitBalances(
            @Valid @RequestBody List<BalanceUpdateDTO> debits) {
        BalanceBulkUpdateResult result = balanceService.debitBalancesFromEmployees(debits);
        return ResponseEntity.ok(result);
    }

    /**
     * Generate balance report for all employees
     */
    @GetMapping("/report")
    public ResponseEntity<List<BalanceReportDTO>> generateBalanceReport() {
        List<BalanceReportDTO> report = balanceService.generateBalanceReport();
        return ResponseEntity.ok(report);
    }

    /**
     * Generate balance report for specific range
     */
    @GetMapping("/report/range")
    public ResponseEntity<List<BalanceReportDTO>> generateBalanceReportByRange(
            @RequestParam BigDecimal minBalance,
            @RequestParam BigDecimal maxBalance) {
        List<BalanceReportDTO> report = balanceService.generateBalanceReportByRange(minBalance, maxBalance);
        return ResponseEntity.ok(report);
    }

    /**
     * Generate negative balance report
     */
    @GetMapping("/report/negative")
    public ResponseEntity<List<BalanceReportDTO>> generateNegativeBalanceReport() {
        List<BalanceReportDTO> report = balanceService.generateNegativeBalanceReport();
        return ResponseEntity.ok(report);
    }

    /**
     * Get count of employees with negative balance
     */
    @GetMapping("/negative/count")
    public ResponseEntity<Long> getNegativeBalanceCount() {
        Long count = balanceService.getEmployeesWithNegativeBalanceCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Get count of employees with zero balance
     */
    @GetMapping("/zero/count")
    public ResponseEntity<Long> getZeroBalanceCount() {
        Long count = balanceService.getEmployeesWithZeroBalanceCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Adjust all negative balances to zero
     */
    @PostMapping("/adjust-negative")
    public ResponseEntity<String> adjustNegativeBalancesToZero() {
        try {
            balanceService.adjustNegativeBalancesToZero();
            return ResponseEntity.ok("Negative balances adjusted to zero successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adjusting balances: " + e.getMessage());
        }
    }

    /**
     * Adjust employee balance to minimum
     */
    @PostMapping("/adjust-minimum/{pin}")
    public ResponseEntity<String> adjustEmployeeBalanceToMinimum(
            @PathVariable String pin,
            @RequestParam BigDecimal minimumBalance) {
        try {
            balanceService.adjustEmployeeBalanceToMinimum(pin, minimumBalance);
            return ResponseEntity.ok("Balance adjusted to minimum for employee: " + pin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adjusting balance: " + e.getMessage());
        }
    }

    /**
     * Get employees requiring balance adjustment
     */
    @GetMapping("/adjustment-required/{minimumBalance}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesRequiringBalanceAdjustment(
            @PathVariable BigDecimal minimumBalance) {
        List<EmployeeDTO> employees = balanceService.getEmployeesRequiringBalanceAdjustment(minimumBalance);
        return ResponseEntity.ok(employees);
    }

    /**
     * Validation endpoints
     */
    @GetMapping("/has-negative/{pin}")
    public ResponseEntity<Boolean> hasNegativeBalance(@PathVariable String pin) {
        boolean hasNegative = balanceService.hasNegativeBalance(pin);
        return ResponseEntity.ok(hasNegative);
    }

    @GetMapping("/has-sufficient/{pin}")
    public ResponseEntity<Boolean> hasSufficientBalance(
            @PathVariable String pin,
            @RequestParam BigDecimal requiredAmount) {
        boolean hasSufficient = balanceService.hasSufficientBalance(pin, requiredAmount);
        return ResponseEntity.ok(hasSufficient);
    }

    /**
     * Administrative operations
     */
    @PostMapping("/freeze/{pin}")
    public ResponseEntity<String> freezeEmployeeBalance(@PathVariable String pin) {
        try {
            balanceService.freezeEmployeeBalance(pin);
            return ResponseEntity.ok("Balance frozen for employee: " + pin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error freezing balance: " + e.getMessage());
        }
    }

    @PostMapping("/unfreeze/{pin}")
    public ResponseEntity<String> unfreezeEmployeeBalance(@PathVariable String pin) {
        try {
            balanceService.unfreezeEmployeeBalance(pin);
            return ResponseEntity.ok("Balance unfrozen for employee: " + pin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error unfreezing balance: " + e.getMessage());
        }
    }

    @GetMapping("/is-frozen/{pin}")
    public ResponseEntity<Boolean> isBalanceFrozen(@PathVariable String pin) {
        boolean isFrozen = balanceService.isBalanceFrozen(pin);
        return ResponseEntity.ok(isFrozen);
    }
}