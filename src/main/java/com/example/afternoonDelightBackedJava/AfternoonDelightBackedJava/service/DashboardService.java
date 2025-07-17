package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.DashboardSummaryDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeeBalanceDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.EmployeeRepository;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final EmployeeRepository employeeRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public DashboardService(EmployeeRepository employeeRepository,
                            TransactionRepository transactionRepository) {
        this.employeeRepository = employeeRepository;
        this.transactionRepository = transactionRepository;
    }

    public DashboardSummaryDTO getDashboardSummary() {
        DashboardSummaryDTO summary = new DashboardSummaryDTO();

        // Calculate total balance
        BigDecimal totalBalance = employeeRepository.sumAllBalances();
        summary.setTotalBalance(totalBalance != null ? totalBalance : BigDecimal.ZERO);

        // Calculate current month costs
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        BigDecimal currentMonthCost = transactionRepository.sumDebitsBetweenDates(
                firstDayOfMonth, lastDayOfMonth);
        summary.setCurrentMonthCost(currentMonthCost != null ? currentMonthCost : BigDecimal.ZERO);

        BigDecimal currentMonthCredit = transactionRepository.sumCreditsBetweenDates(
                firstDayOfMonth, lastDayOfMonth);
        summary.setCurrentMonthCredit(currentMonthCredit != null ? currentMonthCredit : BigDecimal.ZERO);

        // Get employee balance categories
        summary.setNegativeBalanceEmployees(getEmployeesByBalanceRange(
                null, BigDecimal.ZERO.subtract(BigDecimal.ONE)));

        summary.setHighBalanceEmployees(getEmployeesByBalanceRange(
                new BigDecimal(1000), null));

        summary.setLowBalanceEmployees(getEmployeesByBalanceRange(
                BigDecimal.ZERO, new BigDecimal(500)));

        return summary;
    }

    private List<EmployeeBalanceDTO> getEmployeesByBalanceRange(BigDecimal min, BigDecimal max) {
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

        return employees.stream().map(this::convertToBalanceDTO).collect(Collectors.toList());
    }

    private EmployeeBalanceDTO convertToBalanceDTO(Employee employee) {
        EmployeeBalanceDTO dto = new EmployeeBalanceDTO();
        dto.setId(employee.getId());
        dto.setUsername(employee.getUsername());
        dto.setPin(employee.getPin());
        dto.setBalance(employee.getBalance());
        return dto;
    }
}
