package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Meal;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Transaction;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final EmployeeRepository employeeRepository;
    private final TransactionRepository transactionRepository;
    private final MealRepository mealRepository;
    private final MealParticipationRepository participationRepository;
    private final FoodItemRepository foodItemRepository;

    // Employee Balance Report
    public EmployeeBalanceReport generateEmployeeBalanceReport(
            BigDecimal minBalance, BigDecimal maxBalance) {

        EmployeeBalanceReport report = new EmployeeBalanceReport();
        report.setReportDate(LocalDate.now());

        // Total system balance
        BigDecimal totalBalance = employeeRepository.sumAllBalances();
        report.setTotalSystemBalance(totalBalance != null ? totalBalance : BigDecimal.ZERO);

        // Employee counts
        report.setEmployeeCount(employeeRepository.count());

        // Employee list with balances
        List<Employee> employees;
        if (minBalance != null && maxBalance != null) {
            employees = employeeRepository.findByBalanceBetween(minBalance, maxBalance);
        } else if (minBalance != null) {
            employees = employeeRepository.findByBalanceGreaterThanEqual(minBalance);
        } else if (maxBalance != null) {
            employees = employeeRepository.findByBalanceLessThanEqual(maxBalance);
        } else {
            employees = employeeRepository.findAll();
        }

        report.setEmployees(employees.stream()
                .map(this::convertToBalanceItem)
                .collect(Collectors.toList()));

        return report;
    }

    // Transaction History (CSV format)
    public String generateTransactionHistoryCsv(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findByTransactionDateBetween(startDate, endDate);

        StringBuilder csv = new StringBuilder();
        csv.append("Transaction ID,Date,Employee PIN,Amount,Description,Meal\n");

        for (Transaction txn : transactions) {
            csv.append(String.format("%d,%s,%s,%.2f,%s,%s\n",
                    txn.getId(),
                    txn.getTransactionDate(),
                    txn.getEmployee().getPin(),
                    txn.getAmount(),
                    txn.getDescription(),
                    txn.getMeal() != null ? txn.getMeal().getFoodItem().getName() : "N/A"
            ));
        }

        return csv.toString();
    }

    // Meal Participation Report
    public MealParticipationReport generateMealParticipationReport(
            LocalDate startDate, LocalDate endDate) {

        MealParticipationReport report = new MealParticipationReport();
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        // Get meals in date range
        List<Meal> meals = mealRepository.findByMealDateBetween(startDate,endDate);
        report.setTotalMeals(meals.size());

        // Calculate participation statistics
        int totalParticipants = 0;
        BigDecimal totalCost = BigDecimal.ZERO;
        Map<String, MealStat> mealStats = new HashMap<>();

        for (Meal meal : meals) {
            int participants = participationRepository.countByMealId(meal.getId());
            totalParticipants += participants;

            BigDecimal mealCost = meal.getPerHeadCost().multiply(BigDecimal.valueOf(participants));
            totalCost = totalCost.add(mealCost);

            // Per-food statistics
            String foodName = meal.getFoodItem().getName();
            mealStats.computeIfAbsent(foodName, k -> new MealStat())
                    .addMeal(participants, mealCost);
        }

        report.setTotalParticipants(totalParticipants);
        report.setTotalCost(totalCost);

        // Calculate averages
        if (!meals.isEmpty()) {
            report.setAvgParticipantsPerMeal(totalParticipants / (double) meals.size());
            report.setAvgCostPerMeal(totalCost.divide(BigDecimal.valueOf(meals.size()), 2, RoundingMode.HALF_UP));
        }

        // Food item statistics
        report.setMealStats(mealStats);

        return report;
    }

    // Employee Meal History Report
    public EmployeeMealHistoryReport generateEmployeeMealHistory(String pin, int months) {
        Employee employee = employeeRepository.findByPin(pin)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(months);

        EmployeeMealHistoryReport report = new EmployeeMealHistoryReport();
        report.setEmployeeName(employee.getUsername());
        report.setPin(pin);
        report.setPeriod(months + " months");
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        List<Meal> meals = participationRepository.findMealsByEmployeeAndDateRange(employee.getId(), startDate, endDate);
        report.setTotalMeals(meals.size());

        Map<String, Integer> mealFrequency = new HashMap<>();
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Meal meal : meals) {
            String foodName = meal.getFoodItem().getName();
            mealFrequency.put(foodName, mealFrequency.getOrDefault(foodName, 0) + 1);
            totalCost = totalCost.add(meal.getPerHeadCost());
        }

        report.setTotalSpent(totalCost);

        return report;
    }

    // Low Balance Alerts
    public LowBalanceAlertReport generateLowBalanceAlerts(BigDecimal threshold) {
        LowBalanceAlertReport report = new LowBalanceAlertReport();
        report.setThreshold(threshold);
        report.setGeneratedDate(LocalDate.now());

        List<Employee> employees = employeeRepository.findByBalanceLessThan(threshold);
        report.setEmployees(employees.stream()
                .map(this::convertToBalanceItem)
                .collect(Collectors.toList()));

        return report;
    }

    // Food Preference Report
    public FoodPreferenceReport generateFoodPreferenceReport(LocalDate startDate, LocalDate endDate) {
        FoodPreferenceReport report = new FoodPreferenceReport();
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        Map<String, FoodPreferenceStat> stats = new HashMap<>();
        List<Object[]> results = foodItemRepository.getFoodPreferenceStats(startDate, endDate);

        for (Object[] result : results) {
            String foodName = (String) result[0];
            Long totalMeals = (Long) result[1];
            Long totalParticipants = (Long) result[2];

            FoodPreferenceStat stat = new FoodPreferenceStat();
            stat.setTotalMeals(totalMeals);
            stat.setTotalParticipants(totalParticipants);
            stats.put(foodName, stat);
        }

        report.setFoodStats(stats);
        return report;
    }

    // Helper: Convert Employee to DTO
    private EmployeeBalanceItem convertToBalanceItem(Employee employee) {
        EmployeeBalanceItem item = new EmployeeBalanceItem();
        item.setPin(employee.getPin());
        item.setName(employee.getUsername());
        item.setBalance(employee.getBalance());
        return item;
    }
}