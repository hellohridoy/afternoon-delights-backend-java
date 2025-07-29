package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.DashboardSummaryDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeeBalanceDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.EmployeeRepository;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final TransactionRepository transactionRepository;

    public DashboardSummaryDTO getDashboardSummary() {
        log.info("Starting dashboard summary calculation");

        try {
            DashboardSummaryDTO summary = new DashboardSummaryDTO();

            // Calculate total balance
            log.debug("Calculating total balance from all employees");
            BigDecimal totalBalance = calculateTotalBalance();
            summary.setTotalBalance(totalBalance);
            log.debug("Total balance calculated: {}", totalBalance);

            // Calculate current month costs and credits
            LocalDate now = LocalDate.now();
            LocalDate firstDayOfMonth = now.withDayOfMonth(1);
            LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());
            log.debug("Calculating current month transactions from {} to {}", firstDayOfMonth, lastDayOfMonth);

            BigDecimal currentMonthCost = calculateCurrentMonthCost(firstDayOfMonth, lastDayOfMonth);
            summary.setCurrentMonthCost(currentMonthCost);
            log.debug("Current month cost calculated: {}", currentMonthCost);

            BigDecimal currentMonthCredit = calculateCurrentMonthCredit(firstDayOfMonth, lastDayOfMonth);
            summary.setCurrentMonthCredit(currentMonthCredit);
            log.debug("Current month credit calculated: {}", currentMonthCredit);

            // Get employee balance categories
            log.debug("Categorizing employees by balance ranges");
            summary.setNegativeBalanceEmployees(getEmployeesByBalanceRange(
                    null, BigDecimal.ZERO.subtract(BigDecimal.ONE)));
            log.debug("Found {} employees with negative balance", summary.getNegativeBalanceEmployees().size());

            summary.setHighBalanceEmployees(getEmployeesByBalanceRange(
                    new BigDecimal(1000), null));
            log.debug("Found {} employees with high balance (>= 1000)", summary.getHighBalanceEmployees().size());

            summary.setLowBalanceEmployees(getEmployeesByBalanceRange(
                    BigDecimal.ZERO, new BigDecimal(500)));
            log.debug("Found {} employees with low balance (0-500)", summary.getLowBalanceEmployees().size());

            log.info("Dashboard summary calculation completed successfully");
            return summary;

        } catch (DataAccessException e) {
            log.error("Database error occurred while calculating dashboard summary: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve dashboard data due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while calculating dashboard summary: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to calculate dashboard summary", e);
        }
    }

    private BigDecimal calculateTotalBalance() {
        try {
            BigDecimal totalBalance = employeeRepository.sumAllBalances();
            return totalBalance != null ? totalBalance : BigDecimal.ZERO;
        } catch (DataAccessException e) {
            log.error("Error calculating total balance: {}", e.getMessage(), e);
            throw e;
        }
    }

    private BigDecimal calculateCurrentMonthCost(LocalDate firstDayOfMonth, LocalDate lastDayOfMonth) {
        try {
            BigDecimal currentMonthCost = transactionRepository.sumDebitsBetweenDates(
                    firstDayOfMonth, lastDayOfMonth);
            return currentMonthCost != null ? currentMonthCost : BigDecimal.ZERO;
        } catch (DataAccessException e) {
            log.error("Error calculating current month cost from {} to {}: {}",
                    firstDayOfMonth, lastDayOfMonth, e.getMessage(), e);
            throw e;
        }
    }

    private BigDecimal calculateCurrentMonthCredit(LocalDate firstDayOfMonth, LocalDate lastDayOfMonth) {
        try {
            BigDecimal currentMonthCredit = transactionRepository.sumCreditsBetweenDates(
                    firstDayOfMonth, lastDayOfMonth);
            return currentMonthCredit != null ? currentMonthCredit : BigDecimal.ZERO;
        } catch (DataAccessException e) {
            log.error("Error calculating current month credit from {} to {}: {}",
                    firstDayOfMonth, lastDayOfMonth, e.getMessage(), e);
            throw e;
        }
    }

    private List<EmployeeBalanceDTO> getEmployeesByBalanceRange(BigDecimal min, BigDecimal max) {
        try {
            log.debug("Fetching employees with balance range: min={}, max={}", min, max);

            List<Employee> employees;
            if (min == null && max == null) {
                employees = Collections.emptyList();
            } else if (min == null) {
                employees = employeeRepository.findByBalanceLessThanEqual(max);
            } else if (max == null) {
                employees = employeeRepository.findByBalanceGreaterThanEqual(min);
            } else {
                employees = employeeRepository.findByBalanceBetween(min, max);
            }

            List<EmployeeBalanceDTO> result = employees.stream()
                    .map(this::convertToBalanceDTO)
                    .collect(Collectors.toList());

            log.debug("Found {} employees in balance range min={}, max={}", result.size(), min, max);
            return result;

        } catch (DataAccessException e) {
            log.error("Error fetching employees by balance range (min={}, max={}): {}",
                    min, max, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while processing employees by balance range: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process employee balance data", e);
        }
    }

    private EmployeeBalanceDTO convertToBalanceDTO(Employee employee) {
        try {
            if (employee == null) {
                log.warn("Attempted to convert null employee to EmployeeBalanceDTO");
                return null;
            }

            EmployeeBalanceDTO dto = new EmployeeBalanceDTO();
            dto.setId(employee.getId());
            dto.setUsername(employee.getUsername());
            dto.setPin(employee.getPin());
            dto.setBalance(employee.getBalance());

            return dto;

        } catch (Exception e) {
            log.error("Error converting employee {} to EmployeeBalanceDTO: {}",
                    employee != null ? employee.getId() : "null", e.getMessage(), e);
            throw new RuntimeException("Failed to convert employee data", e);
        }
    }
}