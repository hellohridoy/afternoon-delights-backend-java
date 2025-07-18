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
        Employee employee = findEmployeeByPin(pin);
        return employee.getBalance();
    }

    @Override
    public void updateEmployeeBalance(String pin, BigDecimal amount) {
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
        return employeeRepository.findByBalanceLessThan(threshold)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithBalanceLessThanEqual(BigDecimal maxBalance) {
        return employeeRepository.findByBalanceLessThanEqual(maxBalance)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithBalanceGreaterThan(BigDecimal threshold) {
        return employeeRepository.findByBalanceGreaterThan(threshold)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithBalanceGreaterThanEqual(BigDecimal minBalance) {
        return employeeRepository.findByBalanceGreaterThanEqual(minBalance)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithBalanceBetween(BigDecimal minBalance, BigDecimal maxBalance) {
        return employeeRepository.findByBalanceBetween(minBalance, maxBalance)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithExactBalance(BigDecimal exactBalance) {
        return employeeRepository.findByBalance(exactBalance)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Threshold operations
    @Override
    public List<EmployeeDTO> getEmployeesWithLowBalance(BigDecimal threshold) {
        return employeeRepository.findEmployeesWithLowBalance(threshold)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithHighBalance(BigDecimal threshold) {
        return employeeRepository.findEmployeesWithHighBalance(threshold)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getActiveEmployeesInBalanceRange(BigDecimal minBalance, BigDecimal maxBalance) {
        return employeeRepository.findActiveEmployeesByBalanceRange(minBalance, maxBalance)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Aggregations and statistics
    @Override
    public BigDecimal getTotalActiveEmployeeBalances() {
        BigDecimal total = employeeRepository.sumAllBalances();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalAllEmployeeBalances() {
        BigDecimal total = employeeRepository.sumAllBalancesIncludingInactive();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public Long getEmployeesWithNegativeBalanceCount() {
        return employeeRepository.countEmployeesWithNegativeBalance();
    }

    @Override
    public Long getEmployeesWithZeroBalanceCount() {
        return employeeRepository.countEmployeesWithZeroBalance();
    }

    @Override
    public BalanceStatisticsDTO getBalanceStatistics() {
        BigDecimal total = getTotalActiveEmployeeBalances();
        BigDecimal average = employeeRepository.getAverageBalance();
        BigDecimal minimum = employeeRepository.getMinimumBalance();
        BigDecimal maximum = employeeRepository.getMaximumBalance();
        Long negativeCount = getEmployeesWithNegativeBalanceCount();
        Long zeroCount = getEmployeesWithZeroBalanceCount();
        Long totalActiveEmployees = employeeRepository.countByIsActiveTrue();

        return BalanceStatisticsDTO.builder()
                .totalBalance(total)
                .averageBalance(average != null ? average : BigDecimal.ZERO)
                .minimumBalance(minimum != null ? minimum : BigDecimal.ZERO)
                .maximumBalance(maximum != null ? maximum : BigDecimal.ZERO)
                .employeesWithNegativeBalance(negativeCount)
                .employeesWithZeroBalance(zeroCount)
                .totalActiveEmployees(totalActiveEmployees)
                .build();
    }

    // Bulk operations
    @Override
    @Transactional
    public BalanceBulkUpdateResult updateBalancesForEmployees(List<BalanceUpdateDTO> updates) {
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
        return getEmployeesWithBalanceLessThanEqual(minimumRequiredBalance);
    }

    @Override
    @Transactional
    public void adjustNegativeBalancesToZero() {
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
        Employee employee = findEmployeeByPin(pin);

        if (employee.getBalance().compareTo(minimumBalance) < 0) {
            BigDecimal oldBalance = employee.getBalance();
            employee.setBalance(minimumBalance);
            employee.setUpdatedAt(LocalDateTime.now());
            employeeRepository.save(employee);

            log.info("Adjusted balance to minimum for employee {}: {} -> {}",
                    pin, oldBalance, minimumBalance);
        }
    }

    // Reporting and analytics
    @Override
    public List<BalanceReportDTO> generateBalanceReport() {
        List<Employee> employees = employeeRepository.findByIsActiveTrueOrderByUsername();

        return employees.stream()
                .map(this::convertToBalanceReportDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BalanceReportDTO> generateBalanceReportByRange(BigDecimal minBalance, BigDecimal maxBalance) {
        List<Employee> employees = employeeRepository.findActiveEmployeesByBalanceRange(minBalance, maxBalance);

        return employees.stream()
                .map(this::convertToBalanceReportDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BalanceReportDTO> generateNegativeBalanceReport() {
        List<Employee> employees = employeeRepository.findEmployeesWithLowBalance(BigDecimal.ZERO);

        return employees.stream()
                .filter(emp -> emp.getBalance().compareTo(BigDecimal.ZERO) < 0)
                .map(this::convertToBalanceReportDTO)
                .collect(Collectors.toList());
    }

    // Balance history and transactions (placeholder methods - would need transaction history)
    @Override
    public BigDecimal calculateTotalDeductions(String pin) {
        // This would typically query transaction history
        // For now, returning zero as placeholder
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateTotalCredits(String pin) {
        // This would typically query transaction history
        // For now, returning zero as placeholder
        return BigDecimal.ZERO;
    }

    // Validation methods
    @Override
    public boolean hasNegativeBalance(String pin) {
        Employee employee = findEmployeeByPin(pin);
        return employee.getBalance().compareTo(BigDecimal.ZERO) < 0;
    }

    @Override
    public boolean hasSufficientBalance(String pin, BigDecimal requiredAmount) {
        Employee employee = findEmployeeByPin(pin);
        return employee.getBalance().compareTo(requiredAmount) >= 0;
    }

    @Override
    public void validateBalanceOperation(String pin, BigDecimal amount) {
        Employee employee = findEmployeeByPin(pin);

        if (!employee.getIsActive()) {
            throw new IllegalStateException("Cannot perform balance operation on inactive employee: " + pin);
        }

        if (isBalanceFrozen(pin)) {
            throw new IllegalStateException("Balance is frozen for employee: " + pin);
        }

        // Check for potential negative balance on debits
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal resultingBalance = employee.getBalance().add(amount);
            if (resultingBalance.compareTo(new BigDecimal("-1000")) < 0) {
                throw new IllegalArgumentException("Operation would result in balance below minimum threshold for employee: " + pin);
            }
        }
    }

    // Administrative operations
    @Override
    @Transactional
    public void freezeEmployeeBalance(String pin) {
        Employee employee = findEmployeeByPin(pin);
        // This would typically set a flag in the database
        // For now, we'll add a note or use an existing field
        log.info("Balance frozen for employee: {}", pin);
    }

    @Override
    @Transactional
    public void unfreezeEmployeeBalance(String pin) {
        Employee employee = findEmployeeByPin(pin);
        // This would typically unset a flag in the database
        log.info("Balance unfrozen for employee: {}", pin);
    }

    @Override
    public boolean isBalanceFrozen(String pin) {
        // This would typically check a flag in the database
        // For now, returning false as placeholder
        return false;
    }

    // Helper methods
    private Employee findEmployeeByPin(String pin) {
        return employeeRepository.findByPin(pin)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with PIN: " + pin));
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        return modelMapper.map(employee, EmployeeDTO.class);
    }

    private BalanceReportDTO convertToBalanceReportDTO(Employee employee) {
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
