package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.BalanceBulkUpdateResult;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.BalanceReportDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.BalanceStatisticsDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.BalanceUpdateDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for employee balance operations
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BalanceServiceImpl implements BalanceService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    // Basic balance operations
    @Override
    public BigDecimal getEmployeeBalance(String pin) {
        log.debug("Getting balance for employee with PIN: {}", pin);
        Employee employee = findEmployeeByPin(pin);
        BigDecimal balance = employee.getBalance();
        log.debug("Retrieved balance {} for employee {}", balance, pin);
        return balance;
    }

    @Override
    public void updateEmployeeBalance(String pin, BigDecimal amount) {
        log.info("Updating balance for employee {} with amount: {}", pin, amount);
        Employee employee = findEmployeeByPin(pin);
        BigDecimal oldBalance = employee.getBalance();
        BigDecimal newBalance = oldBalance.add(amount);

        employee.setBalance(newBalance);
        employee.setUpdatedAt(LocalDateTime.now());
        employeeRepository.save(employee);

        log.info("Updated balance for employee {}: {} -> {} (change: {})",
                pin, oldBalance, newBalance, amount);
    }

    @Override
    public void setEmployeeBalance(String pin, BigDecimal newBalance) {
        log.info("Setting balance for employee {} to: {}", pin, newBalance);
        Employee employee = findEmployeeByPin(pin);
        BigDecimal oldBalance = employee.getBalance();

        employee.setBalance(newBalance);
        employee.setUpdatedAt(LocalDateTime.now());
        employeeRepository.save(employee);

        log.info("Set balance for employee {}: {} -> {}", pin, oldBalance, newBalance);
    }

    // Range queries
    @Override
    public List<EmployeeDTO> getEmployeesWithBalanceLessThan(BigDecimal threshold) {
        log.debug("Finding employees with balance less than: {}", threshold);
        List<EmployeeDTO> employees = employeeRepository.findByBalanceLessThan(threshold)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        log.debug("Found {} employees with balance less than {}", employees.size(), threshold);
        return employees;
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithBalanceLessThanEqual(BigDecimal maxBalance) {
        log.debug("Finding employees with balance less than or equal to: {}", maxBalance);
        List<EmployeeDTO> employees = employeeRepository.findByBalanceLessThanEqual(maxBalance)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        log.debug("Found {} employees with balance <= {}", employees.size(), maxBalance);
        return employees;
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithBalanceGreaterThan(BigDecimal threshold) {
        log.debug("Finding employees with balance greater than: {}", threshold);
        List<EmployeeDTO> employees = employeeRepository.findByBalanceGreaterThan(threshold)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        log.debug("Found {} employees with balance > {}", employees.size(), threshold);
        return employees;
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithBalanceGreaterThanEqual(BigDecimal minBalance) {
        log.debug("Finding employees with balance greater than or equal to: {}", minBalance);
        List<EmployeeDTO> employees = employeeRepository.findByBalanceGreaterThanEqual(minBalance)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        log.debug("Found {} employees with balance >= {}", employees.size(), minBalance);
        return employees;
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithBalanceBetween(BigDecimal minBalance, BigDecimal maxBalance) {
        log.debug("Finding employees with balance between {} and {}", minBalance, maxBalance);
        List<EmployeeDTO> employees = employeeRepository.findByBalanceBetween(minBalance, maxBalance)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        log.debug("Found {} employees with balance between {} and {}", employees.size(), minBalance, maxBalance);
        return employees;
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithExactBalance(BigDecimal exactBalance) {
        log.debug("Finding employees with exact balance: {}", exactBalance);
        List<EmployeeDTO> employees = employeeRepository.findByBalance(exactBalance)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        log.debug("Found {} employees with exact balance {}", employees.size(), exactBalance);
        return employees;
    }

    // Threshold operations
    @Override
    public List<EmployeeDTO> getEmployeesWithLowBalance(BigDecimal threshold) {
        log.info("Finding employees with low balance below threshold: {}", threshold);
        List<EmployeeDTO> employees = employeeRepository.findEmployeesWithLowBalance(threshold)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        log.info("Found {} employees with low balance", employees.size());
        return employees;
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithHighBalance(BigDecimal threshold) {
        log.info("Finding employees with high balance above threshold: {}", threshold);
        List<EmployeeDTO> employees = employeeRepository.findEmployeesWithHighBalance(threshold)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        log.info("Found {} employees with high balance", employees.size());
        return employees;
    }

    @Override
    public List<EmployeeDTO> getActiveEmployeesInBalanceRange(BigDecimal minBalance, BigDecimal maxBalance) {
        log.debug("Finding active employees with balance between {} and {}", minBalance, maxBalance);
        List<EmployeeDTO> employees = employeeRepository.findActiveEmployeesByBalanceRange(minBalance, maxBalance)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        log.debug("Found {} active employees in balance range", employees.size());
        return employees;
    }

    // Aggregations and statistics
    @Override
    public BigDecimal getTotalActiveEmployeeBalances() {
        log.debug("Calculating total active employee balances");
        BigDecimal total = employeeRepository.sumAllBalances();
        BigDecimal result = total != null ? total : BigDecimal.ZERO;
        log.debug("Total active employee balances: {}", result);
        return result;
    }

    @Override
    public BigDecimal getTotalAllEmployeeBalances() {
        log.debug("Calculating total all employee balances (including inactive)");
        BigDecimal total = employeeRepository.sumAllBalancesIncludingInactive();
        BigDecimal result = total != null ? total : BigDecimal.ZERO;
        log.debug("Total all employee balances: {}", result);
        return result;
    }

    @Override
    public Long getEmployeesWithNegativeBalanceCount() {
        log.debug("Counting employees with negative balance");
        Long count = employeeRepository.countEmployeesWithNegativeBalance();
        log.debug("Found {} employees with negative balance", count);
        return count;
    }

    @Override
    public Long getEmployeesWithZeroBalanceCount() {
        log.debug("Counting employees with zero balance");
        Long count = employeeRepository.countEmployeesWithZeroBalance();
        log.debug("Found {} employees with zero balance", count);
        return count;
    }

    @Override
    public BalanceStatisticsDTO getBalanceStatistics() {
        log.info("Generating balance statistics");
        BigDecimal total = getTotalActiveEmployeeBalances();
        BigDecimal average = employeeRepository.getAverageBalance();
        BigDecimal minimum = employeeRepository.getMinimumBalance();
        BigDecimal maximum = employeeRepository.getMaximumBalance();
        Long negativeCount = getEmployeesWithNegativeBalanceCount();
        Long zeroCount = getEmployeesWithZeroBalanceCount();
        Long totalActiveEmployees = employeeRepository.countByIsActiveTrue();

        BalanceStatisticsDTO stats = BalanceStatisticsDTO.builder()
                .totalBalance(total)
                .averageBalance(average != null ? average : BigDecimal.ZERO)
                .minimumBalance(minimum != null ? minimum : BigDecimal.ZERO)
                .maximumBalance(maximum != null ? maximum : BigDecimal.ZERO)
                .employeesWithNegativeBalance(negativeCount)
                .employeesWithZeroBalance(zeroCount)
                .totalActiveEmployees(totalActiveEmployees)
                .build();

        log.info("Balance statistics generated - Total: {}, Average: {}, Active employees: {}",
                total, stats.getAverageBalance(), totalActiveEmployees);
        return stats;
    }

    // Bulk operations
    @Override
    @Transactional
    public BalanceBulkUpdateResult updateBalancesForEmployees(List<BalanceUpdateDTO> updates) {
        log.info("Starting bulk balance update for {} employees", updates.size());
        List<String> successful = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        BigDecimal totalAmountProcessed = BigDecimal.ZERO;

        for (BalanceUpdateDTO update : updates) {
            try {
                validateBalanceOperation(update.getPin(), update.getAmount());
                updateEmployeeBalance(update.getPin(), update.getAmount());

                successful.add(update.getPin());
                totalAmountProcessed = totalAmountProcessed.add(update.getAmount());

            } catch (Exception e) {
                failed.add(update.getPin() + ": " + e.getMessage());
                log.error("Failed to update balance for employee {}: {}",
                        update.getPin(), e.getMessage());
            }
        }

        log.info("Bulk balance update completed. Successful: {}, Failed: {}, Total amount: {}",
                successful.size(), failed.size(), totalAmountProcessed);

        return BalanceBulkUpdateResult.builder()
                .totalRequested(updates.size())
                .successful(successful.size())
                .failed(failed.size())
                .successfulPins(successful)
                .failedPins(failed)
                .totalAmountProcessed(totalAmountProcessed)
                .build();
    }

    @Override
    @Transactional
    public BalanceBulkUpdateResult creditBalancesToEmployees(List<BalanceUpdateDTO> credits) {
        log.info("Processing bulk credit operations for {} employees", credits.size());
        // Ensure all amounts are positive for credits
        List<BalanceUpdateDTO> positiveCredits = credits.stream()
                .map(credit -> BalanceUpdateDTO.builder()
                        .pin(credit.getPin())
                        .amount(credit.getAmount().abs())
                        .description("Credit: " + credit.getDescription())
                        .reason(credit.getReason())
                        .build())
                .collect(Collectors.toList());

        return updateBalancesForEmployees(positiveCredits);
    }

    @Override
    @Transactional
    public BalanceBulkUpdateResult debitBalancesFromEmployees(List<BalanceUpdateDTO> debits) {
        log.info("Processing bulk debit operations for {} employees", debits.size());
        // Ensure all amounts are negative for debits
        List<BalanceUpdateDTO> negativeDebits = debits.stream()
                .map(debit -> BalanceUpdateDTO.builder()
                        .pin(debit.getPin())
                        .amount(debit.getAmount().abs().negate())
                        .description("Debit: " + debit.getDescription())
                        .reason(debit.getReason())
                        .build())
                .collect(Collectors.toList());

        return updateBalancesForEmployees(negativeDebits);
    }

    // Balance adjustments and validations
    @Override
    public List<EmployeeDTO> getEmployeesRequiringBalanceAdjustment(BigDecimal minimumRequiredBalance) {
        log.info("Finding employees requiring balance adjustment to minimum: {}", minimumRequiredBalance);
        List<EmployeeDTO> employees = getEmployeesWithBalanceLessThanEqual(minimumRequiredBalance);
        log.info("Found {} employees requiring balance adjustment", employees.size());
        return employees;
    }

    @Override
    @Transactional
    public void adjustNegativeBalancesToZero() {
        log.info("Starting adjustment of negative balances to zero");
        List<Employee> employeesWithNegativeBalance = employeeRepository
                .findByBalanceLessThanEqual(BigDecimal.valueOf(-0.01));

        int adjustedCount = 0;
        for (Employee employee : employeesWithNegativeBalance) {
            if (employee.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal oldBalance = employee.getBalance();
                employee.setBalance(BigDecimal.ZERO);
                employee.setUpdatedAt(LocalDateTime.now());
                employeeRepository.save(employee);
                adjustedCount++;

                log.info("Adjusted negative balance to zero for employee {}: {} -> 0",
                        employee.getPin(), oldBalance);
            }
        }

        log.info("Adjusted {} employees with negative balances to zero", adjustedCount);
    }

    @Override
    @Transactional
    public void adjustEmployeeBalanceToMinimum(String pin, BigDecimal minimumBalance) {
        log.info("Adjusting employee {} balance to minimum: {}", pin, minimumBalance);
        Employee employee = findEmployeeByPin(pin);

        if (employee.getBalance().compareTo(minimumBalance) < 0) {
            BigDecimal oldBalance = employee.getBalance();
            employee.setBalance(minimumBalance);
            employee.setUpdatedAt(LocalDateTime.now());
            employeeRepository.save(employee);

            log.info("Adjusted balance to minimum for employee {}: {} -> {}",
                    pin, oldBalance, minimumBalance);
        } else {
            log.debug("Employee {} already has balance {} above minimum {}",
                    pin, employee.getBalance(), minimumBalance);
        }
    }

    // Reporting and analytics
    @Override
    public List<BalanceReportDTO> generateBalanceReport() {
        log.info("Generating complete balance report");
        List<Employee> employees = employeeRepository.findByIsActiveTrueOrderByUsername();
        List<BalanceReportDTO> report = employees.stream()
                .map(this::convertToBalanceReportDTO)
                .collect(Collectors.toList());
        log.info("Generated balance report for {} active employees", report.size());
        return report;
    }

    @Override
    public List<BalanceReportDTO> generateBalanceReportByRange(BigDecimal minBalance, BigDecimal maxBalance) {
        log.info("Generating balance report for range {} to {}", minBalance, maxBalance);
        List<Employee> employees = employeeRepository.findActiveEmployeesByBalanceRange(minBalance, maxBalance);
        List<BalanceReportDTO> report = employees.stream()
                .map(this::convertToBalanceReportDTO)
                .collect(Collectors.toList());
        log.info("Generated balance report for {} employees in range", report.size());
        return report;
    }

    @Override
    public List<BalanceReportDTO> generateNegativeBalanceReport() {
        log.info("Generating negative balance report");
        List<Employee> employees = employeeRepository.findEmployeesWithLowBalance(BigDecimal.ZERO);
        List<BalanceReportDTO> report = employees.stream()
                .filter(emp -> emp.getBalance().compareTo(BigDecimal.ZERO) < 0)
                .map(this::convertToBalanceReportDTO)
                .collect(Collectors.toList());
        log.info("Generated negative balance report for {} employees", report.size());
        return report;
    }

    // Balance history and transactions (placeholder methods - would need transaction history)
    @Override
    public BigDecimal calculateTotalDeductions(String pin) {
        log.debug("Calculating total deductions for employee: {}", pin);
        // This would typically query transaction history
        // For now, returning zero as placeholder
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateTotalCredits(String pin) {
        log.debug("Calculating total credits for employee: {}", pin);
        // This would typically query transaction history
        // For now, returning zero as placeholder
        return BigDecimal.ZERO;
    }

    // Validation methods
    @Override
    public boolean hasNegativeBalance(String pin) {
        log.trace("Checking if employee {} has negative balance", pin);
        Employee employee = findEmployeeByPin(pin);
        boolean hasNegative = employee.getBalance().compareTo(BigDecimal.ZERO) < 0;
        log.trace("Employee {} negative balance check: {}", pin, hasNegative);
        return hasNegative;
    }

    @Override
    public boolean hasSufficientBalance(String pin, BigDecimal requiredAmount) {
        log.trace("Checking if employee {} has sufficient balance for amount: {}", pin, requiredAmount);
        Employee employee = findEmployeeByPin(pin);
        boolean hasSufficient = employee.getBalance().compareTo(requiredAmount) >= 0;
        log.trace("Employee {} sufficient balance check: {} (current: {})",
                pin, hasSufficient, employee.getBalance());
        return hasSufficient;
    }

    @Override
    public void validateBalanceOperation(String pin, BigDecimal amount) {
        log.debug("Validating balance operation for employee {} with amount: {}", pin, amount);
        Employee employee = findEmployeeByPin(pin);

        if (!employee.getIsActive()) {
            log.warn("Attempted balance operation on inactive employee: {}", pin);
            throw new IllegalStateException("Cannot perform balance operation on inactive employee: " + pin);
        }

        if (isBalanceFrozen(pin)) {
            log.warn("Attempted balance operation on frozen account: {}", pin);
            throw new IllegalStateException("Balance is frozen for employee: " + pin);
        }

        // Check for potential negative balance on debits
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal resultingBalance = employee.getBalance().add(amount);
            if (resultingBalance.compareTo(new BigDecimal("-1000")) < 0) {
                log.warn("Balance operation would result in balance below minimum threshold for employee: {}", pin);
                throw new IllegalArgumentException("Operation would result in balance below minimum threshold for employee: " + pin);
            }
        }
        log.debug("Balance operation validation passed for employee: {}", pin);
    }

    // Administrative operations
    @Override
    @Transactional
    public void freezeEmployeeBalance(String pin) {
        log.info("Freezing balance for employee: {}", pin);
        Employee employee = findEmployeeByPin(pin);
        // This would typically set a flag in the database
        // For now, we'll add a note or use an existing field
        log.info("Balance frozen for employee: {}", pin);
    }

    @Override
    @Transactional
    public void unfreezeEmployeeBalance(String pin) {
        log.info("Unfreezing balance for employee: {}", pin);
        Employee employee = findEmployeeByPin(pin);
        // This would typically unset a flag in the database
        log.info("Balance unfrozen for employee: {}", pin);
    }

    @Override
    public boolean isBalanceFrozen(String pin) {
        log.trace("Checking if balance is frozen for employee: {}", pin);
        // This would typically check a flag in the database
        // For now, returning false as placeholder
        return false;
    }

    // Helper methods
    private Employee findEmployeeByPin(String pin) {
        log.trace("Finding employee by PIN: {}", pin);
        return employeeRepository.findByPin(pin)
                .orElseThrow(() -> {
                    log.error("Employee not found with PIN: {}", pin);
                    return new EntityNotFoundException("Employee not found with PIN: " + pin);
                });
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        log.trace("Converting employee {} to DTO", employee.getPin());
        return modelMapper.map(employee, EmployeeDTO.class);
    }

    private BalanceReportDTO convertToBalanceReportDTO(Employee employee) {
        log.trace("Converting employee {} to balance report DTO", employee.getPin());
        return BalanceReportDTO.builder()
                .pin(employee.getPin())
                .username(employee.getUsername())
                .email(employee.getEmail())
                .designation(employee.getDesignation())
                .balance(employee.getBalance())
                .balanceStatus(getBalanceStatus(employee.getBalance()))
                .build();
    }

    private String getBalanceStatus(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            return "NEGATIVE";
        } else if (balance.compareTo(BigDecimal.ZERO) == 0) {
            return "ZERO";
        } else if (balance.compareTo(new BigDecimal("100")) < 0) {
            return "LOW";
        } else if (balance.compareTo(new BigDecimal("1000")) > 0) {
            return "HIGH";
        } else {
            return "NORMAL";
        }
    }
}