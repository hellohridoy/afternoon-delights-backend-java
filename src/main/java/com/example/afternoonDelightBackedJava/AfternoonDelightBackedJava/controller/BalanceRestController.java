package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.controller;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for employee balance operations
 * Uses dedicated BalanceService for separation of concerns
 */
@Slf4j
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
        log.info("Received request to get balance statistics");

        try {
            BalanceStatisticsDTO statistics = balanceService.getBalanceStatistics();
            log.info("Successfully retrieved balance statistics - Total employees: {}, Total balance: {}",
                    statistics.getTotalActiveEmployees(), statistics.getTotalBalance());
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("Error retrieving balance statistics", e);
            throw e;
        }
    }

    /**
     * Get employees with balance less than specified amount (strict)
     */
    @GetMapping("/less-than/{threshold}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithBalanceLessThan(
            @PathVariable BigDecimal threshold) {
        log.info("Received request to get employees with balance less than: {}", threshold);

        try {
            List<EmployeeDTO> employees = balanceService.getEmployeesWithBalanceLessThan(threshold);
            log.info("Successfully retrieved {} employees with balance less than {}",
                    employees.size(), threshold);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error retrieving employees with balance less than: {}", threshold, e);
            throw e;
        }
    }

    /**
     * Get employees with balance less than or equal to specified amount
     */
    @GetMapping("/less-than-equal/{maxBalance}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithBalanceLessThanEqual(
            @PathVariable BigDecimal maxBalance) {
        log.info("Received request to get employees with balance less than or equal to: {}", maxBalance);

        try {
            List<EmployeeDTO> employees = balanceService.getEmployeesWithBalanceLessThanEqual(maxBalance);
            log.info("Successfully retrieved {} employees with balance <= {}",
                    employees.size(), maxBalance);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error retrieving employees with balance <= {}", maxBalance, e);
            throw e;
        }
    }

    /**
     * Get employees with balance greater than specified amount (strict)
     */
    @GetMapping("/greater-than/{threshold}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithBalanceGreaterThan(
            @PathVariable BigDecimal threshold) {
        log.info("Received request to get employees with balance greater than: {}", threshold);

        try {
            List<EmployeeDTO> employees = balanceService.getEmployeesWithBalanceGreaterThan(threshold);
            log.info("Successfully retrieved {} employees with balance > {}",
                    employees.size(), threshold);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error retrieving employees with balance > {}", threshold, e);
            throw e;
        }
    }

    /**
     * Get employees with balance greater than or equal to specified amount
     */
    @GetMapping("/greater-than-equal/{minBalance}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithBalanceGreaterThanEqual(
            @PathVariable BigDecimal minBalance) {
        log.info("Received request to get employees with balance greater than or equal to: {}", minBalance);

        try {
            List<EmployeeDTO> employees = balanceService.getEmployeesWithBalanceGreaterThanEqual(minBalance);
            log.info("Successfully retrieved {} employees with balance >= {}",
                    employees.size(), minBalance);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error retrieving employees with balance >= {}", minBalance, e);
            throw e;
        }
    }

    /**
     * Get employees with exact balance amount
     */
    @GetMapping("/exact/{balance}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithExactBalance(
            @PathVariable BigDecimal balance) {
        log.info("Received request to get employees with exact balance: {}", balance);

        try {
            List<EmployeeDTO> employees = balanceService.getEmployeesWithExactBalance(balance);
            log.info("Successfully retrieved {} employees with exact balance {}",
                    employees.size(), balance);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error retrieving employees with exact balance: {}", balance, e);
            throw e;
        }
    }

    /**
     * Get employees with balance between two amounts
     */
    @GetMapping("/between")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithBalanceBetween(
            @RequestParam BigDecimal minBalance,
            @RequestParam BigDecimal maxBalance) {
        log.info("Received request to get employees with balance between {} and {}",
                minBalance, maxBalance);

        try {
            List<EmployeeDTO> employees = balanceService.getEmployeesWithBalanceBetween(minBalance, maxBalance);
            log.info("Successfully retrieved {} employees with balance between {} and {}",
                    employees.size(), minBalance, maxBalance);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error retrieving employees with balance between {} and {}",
                    minBalance, maxBalance, e);
            throw e;
        }
    }

    /**
     * Get total balance of all active employees
     */
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalActiveEmployeeBalances() {
        log.info("Received request to get total active employee balances");

        try {
            BigDecimal total = balanceService.getTotalActiveEmployeeBalances();
            log.info("Successfully retrieved total active employee balances: {}", total);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            log.error("Error retrieving total active employee balances", e);
            throw e;
        }
    }

    /**
     * Get employees with low balance (below threshold)
     */
    @GetMapping("/low/{threshold}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithLowBalance(
            @PathVariable BigDecimal threshold) {
        log.info("Received request to get employees with low balance below threshold: {}", threshold);

        try {
            List<EmployeeDTO> employees = balanceService.getEmployeesWithLowBalance(threshold);
            log.info("Successfully retrieved {} employees with low balance below {}",
                    employees.size(), threshold);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error retrieving employees with low balance below: {}", threshold, e);
            throw e;
        }
    }

    /**
     * Get employees with high balance (above threshold)
     */
    @GetMapping("/high/{threshold}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesWithHighBalance(
            @PathVariable BigDecimal threshold) {
        log.info("Received request to get employees with high balance above threshold: {}", threshold);

        try {
            List<EmployeeDTO> employees = balanceService.getEmployeesWithHighBalance(threshold);
            log.info("Successfully retrieved {} employees with high balance above {}",
                    employees.size(), threshold);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error retrieving employees with high balance above: {}", threshold, e);
            throw e;
        }
    }

    /**
     * Update balance for a single employee
     */
    @PutMapping("/update/{pin}")
    public ResponseEntity<String> updateEmployeeBalance(
            @PathVariable String pin,
            @RequestParam BigDecimal amount) {
        log.info("Received request to update balance for employee {} by amount: {}", pin, amount);

        try {
            balanceService.updateEmployeeBalance(pin, amount);
            log.info("Successfully updated balance for employee: {}", pin);
            return ResponseEntity.ok("Balance updated successfully for employee: " + pin);
        } catch (Exception e) {
            log.error("Error updating balance for employee {} with amount: {}", pin, amount, e);
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
        log.info("Received request to set balance for employee {} to: {}", pin, newBalance);

        try {
            balanceService.setEmployeeBalance(pin, newBalance);
            log.info("Successfully set balance for employee: {} to: {}", pin, newBalance);
            return ResponseEntity.ok("Balance set successfully for employee: " + pin);
        } catch (Exception e) {
            log.error("Error setting balance for employee {} to: {}", pin, newBalance, e);
            return ResponseEntity.badRequest().body("Error setting balance: " + e.getMessage());
        }
    }

    /**
     * Get current balance for an employee
     */
    @GetMapping("/{pin}")
    public ResponseEntity<BigDecimal> getEmployeeBalance(@PathVariable String pin) {
        log.info("Received request to get balance for employee: {}", pin);

        try {
            BigDecimal balance = balanceService.getEmployeeBalance(pin);
            log.info("Successfully retrieved balance {} for employee: {}", balance, pin);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            log.error("Error retrieving balance for employee: {}", pin, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Bulk update balances for multiple employees
     */
    @PostMapping("/bulk-update")
    public ResponseEntity<BalanceBulkUpdateResult> bulkUpdateBalances(
            @Valid @RequestBody BulkBalanceUpdateRequestDTO request) {
        log.info("Received request for bulk balance update for {} employees",
                request.getUpdates().size());

        try {
            BalanceBulkUpdateResult result = balanceService.updateBalancesForEmployees(request.getUpdates());
            log.info("Bulk balance update completed - Successful: {}, Failed: {}, Total processed: {}",
                    result.getSuccessful(), result.getFailed(), result.getTotalRequested());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error during bulk balance update for {} employees",
                    request.getUpdates().size(), e);
            throw e;
        }
    }

    /**
     * Bulk credit balances (ensure positive amounts)
     */
    @PostMapping("/bulk-credit")
    public ResponseEntity<BalanceBulkUpdateResult> bulkCreditBalances(
            @Valid @RequestBody List<BalanceUpdateDTO> credits) {
        log.info("Received request for bulk credit operation for {} employees", credits.size());

        try {
            BalanceBulkUpdateResult result = balanceService.creditBalancesToEmployees(credits);
            log.info("Bulk credit operation completed - Successful: {}, Failed: {}, Total amount: {}",
                    result.getSuccessful(), result.getFailed(), result.getTotalAmountProcessed());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error during bulk credit operation for {} employees", credits.size(), e);
            throw e;
        }
    }

    /**
     * Bulk debit balances (ensure negative amounts)
     */
    @PostMapping("/bulk-debit")
    public ResponseEntity<BalanceBulkUpdateResult> bulkDebitBalances(
            @Valid @RequestBody List<BalanceUpdateDTO> debits) {
        log.info("Received request for bulk debit operation for {} employees", debits.size());

        try {
            BalanceBulkUpdateResult result = balanceService.debitBalancesFromEmployees(debits);
            log.info("Bulk debit operation completed - Successful: {}, Failed: {}, Total amount: {}",
                    result.getSuccessful(), result.getFailed(), result.getTotalAmountProcessed());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error during bulk debit operation for {} employees", debits.size(), e);
            throw e;
        }
    }

    /**
     * Generate balance report for all employees
     */
    @GetMapping("/report")
    public ResponseEntity<List<BalanceReportDTO>> generateBalanceReport() {
        log.info("Received request to generate balance report for all employees");

        try {
            List<BalanceReportDTO> report = balanceService.generateBalanceReport();
            log.info("Successfully generated balance report for {} employees", report.size());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Error generating balance report", e);
            throw e;
        }
    }

    /**
     * Generate balance report for specific range
     */
    @GetMapping("/report/range")
    public ResponseEntity<List<BalanceReportDTO>> generateBalanceReportByRange(
            @RequestParam BigDecimal minBalance,
            @RequestParam BigDecimal maxBalance) {
        log.info("Received request to generate balance report for range {} to {}",
                minBalance, maxBalance);

        try {
            List<BalanceReportDTO> report = balanceService.generateBalanceReportByRange(minBalance, maxBalance);
            log.info("Successfully generated balance report for {} employees in range {} to {}",
                    report.size(), minBalance, maxBalance);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Error generating balance report for range {} to {}",
                    minBalance, maxBalance, e);
            throw e;
        }
    }

    /**
     * Generate negative balance report
     */
    @GetMapping("/report/negative")
    public ResponseEntity<List<BalanceReportDTO>> generateNegativeBalanceReport() {
        log.info("Received request to generate negative balance report");

        try {
            List<BalanceReportDTO> report = balanceService.generateNegativeBalanceReport();
            log.info("Successfully generated negative balance report for {} employees", report.size());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Error generating negative balance report", e);
            throw e;
        }
    }

    /**
     * Get count of employees with negative balance
     */
    @GetMapping("/negative/count")
    public ResponseEntity<Long> getNegativeBalanceCount() {
        log.info("Received request to get count of employees with negative balance");

        try {
            Long count = balanceService.getEmployeesWithNegativeBalanceCount();
            log.info("Successfully retrieved negative balance count: {}", count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error retrieving negative balance count", e);
            throw e;
        }
    }

    /**
     * Get count of employees with zero balance
     */
    @GetMapping("/zero/count")
    public ResponseEntity<Long> getZeroBalanceCount() {
        log.info("Received request to get count of employees with zero balance");

        try {
            Long count = balanceService.getEmployeesWithZeroBalanceCount();
            log.info("Successfully retrieved zero balance count: {}", count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error retrieving zero balance count", e);
            throw e;
        }
    }

    /**
     * Adjust all negative balances to zero
     */
    @PostMapping("/adjust-negative")
    public ResponseEntity<String> adjustNegativeBalancesToZero() {
        log.info("Received request to adjust all negative balances to zero");

        try {
            balanceService.adjustNegativeBalancesToZero();
            log.info("Successfully adjusted all negative balances to zero");
            return ResponseEntity.ok("Negative balances adjusted to zero successfully");
        } catch (Exception e) {
            log.error("Error adjusting negative balances to zero", e);
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
        log.info("Received request to adjust employee {} balance to minimum: {}", pin, minimumBalance);

        try {
            balanceService.adjustEmployeeBalanceToMinimum(pin, minimumBalance);
            log.info("Successfully adjusted employee {} balance to minimum: {}", pin, minimumBalance);
            return ResponseEntity.ok("Balance adjusted to minimum for employee: " + pin);
        } catch (Exception e) {
            log.error("Error adjusting employee {} balance to minimum: {}", pin, minimumBalance, e);
            return ResponseEntity.badRequest().body("Error adjusting balance: " + e.getMessage());
        }
    }

    /**
     * Get employees requiring balance adjustment
     */
    @GetMapping("/adjustment-required/{minimumBalance}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesRequiringBalanceAdjustment(
            @PathVariable BigDecimal minimumBalance) {
        log.info("Received request to get employees requiring balance adjustment to minimum: {}",
                minimumBalance);

        try {
            List<EmployeeDTO> employees = balanceService.getEmployeesRequiringBalanceAdjustment(minimumBalance);
            log.info("Successfully retrieved {} employees requiring balance adjustment to minimum: {}",
                    employees.size(), minimumBalance);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error retrieving employees requiring balance adjustment to minimum: {}",
                    minimumBalance, e);
            throw e;
        }
    }

    /**
     * Validation endpoints
     */
    @GetMapping("/has-negative/{pin}")
    public ResponseEntity<Boolean> hasNegativeBalance(@PathVariable String pin) {
        log.debug("Received request to check if employee {} has negative balance", pin);

        try {
            boolean hasNegative = balanceService.hasNegativeBalance(pin);
            log.debug("Employee {} negative balance check result: {}", pin, hasNegative);
            return ResponseEntity.ok(hasNegative);
        } catch (Exception e) {
            log.error("Error checking negative balance for employee: {}", pin, e);
            throw e;
        }
    }

    @GetMapping("/has-sufficient/{pin}")
    public ResponseEntity<Boolean> hasSufficientBalance(
            @PathVariable String pin,
            @RequestParam BigDecimal requiredAmount) {
        log.debug("Received request to check if employee {} has sufficient balance for amount: {}",
                pin, requiredAmount);

        try {
            boolean hasSufficient = balanceService.hasSufficientBalance(pin, requiredAmount);
            log.debug("Employee {} sufficient balance check for amount {}: {}",
                    pin, requiredAmount, hasSufficient);
            return ResponseEntity.ok(hasSufficient);
        } catch (Exception e) {
            log.error("Error checking sufficient balance for employee {} and amount: {}",
                    pin, requiredAmount, e);
            throw e;
        }
    }

    /**
     * Administrative operations
     */
    @PostMapping("/freeze/{pin}")
    public ResponseEntity<String> freezeEmployeeBalance(@PathVariable String pin) {
        log.info("Received request to freeze balance for employee: {}", pin);

        try {
            balanceService.freezeEmployeeBalance(pin);
            log.info("Successfully froze balance for employee: {}", pin);
            return ResponseEntity.ok("Balance frozen for employee: " + pin);
        } catch (Exception e) {
            log.error("Error freezing balance for employee: {}", pin, e);
            return ResponseEntity.badRequest().body("Error freezing balance: " + e.getMessage());
        }
    }

    @PostMapping("/unfreeze/{pin}")
    public ResponseEntity<String> unfreezeEmployeeBalance(@PathVariable String pin) {
        log.info("Received request to unfreeze balance for employee: {}", pin);

        try {
            balanceService.unfreezeEmployeeBalance(pin);
            log.info("Successfully unfroze balance for employee: {}", pin);
            return ResponseEntity.ok("Balance unfrozen for employee: " + pin);
        } catch (Exception e) {
            log.error("Error unfreezing balance for employee: {}", pin, e);
            return ResponseEntity.badRequest().body("Error unfreezing balance: " + e.getMessage());
        }
    }

    @GetMapping("/is-frozen/{pin}")
    public ResponseEntity<Boolean> isBalanceFrozen(@PathVariable String pin) {
        log.debug("Received request to check if balance is frozen for employee: {}", pin);

        try {
            boolean isFrozen = balanceService.isBalanceFrozen(pin);
            log.debug("Employee {} balance frozen status: {}", pin, isFrozen);
            return ResponseEntity.ok(isFrozen);
        } catch (Exception e) {
            log.error("Error checking frozen status for employee: {}", pin, e);
            throw e;
        }
    }
}