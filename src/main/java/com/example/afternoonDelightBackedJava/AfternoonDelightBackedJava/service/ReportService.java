package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Meal;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Transaction;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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

        log.info("Starting employee balance report generation with minBalance: {} and maxBalance: {}",
                minBalance, maxBalance);

        try {
            EmployeeBalanceReport report = new EmployeeBalanceReport();
            report.setReportDate(LocalDate.now());

            // Total system balance
            BigDecimal totalBalance = employeeRepository.sumAllBalances();
            report.setTotalSystemBalance(totalBalance != null ? totalBalance : BigDecimal.ZERO);
            log.debug("Total system balance calculated: {}", report.getTotalSystemBalance());

            // Employee counts
            long employeeCount = employeeRepository.count();
            report.setEmployeeCount(employeeCount);
            log.debug("Total employee count: {}", employeeCount);

            // Employee list with balances
            List<Employee> employees;
            if (minBalance != null && maxBalance != null) {
                employees = employeeRepository.findByBalanceBetween(minBalance, maxBalance);
                log.debug("Found {} employees with balance between {} and {}",
                        employees.size(), minBalance, maxBalance);
            } else if (minBalance != null) {
                employees = employeeRepository.findByBalanceGreaterThanEqual(minBalance);
                log.debug("Found {} employees with balance >= {}", employees.size(), minBalance);
            } else if (maxBalance != null) {
                employees = employeeRepository.findByBalanceLessThanEqual(maxBalance);
                log.debug("Found {} employees with balance <= {}", employees.size(), maxBalance);
            } else {
                employees = employeeRepository.findAll();
                log.debug("Found {} total employees", employees.size());
            }

            report.setEmployees(employees.stream()
                    .map(this::convertToBalanceItem)
                    .collect(Collectors.toList()));

            log.info("Employee balance report generated successfully with {} employees",
                    report.getEmployees().size());
            return report;

        } catch (Exception e) {
            log.error("Error generating employee balance report", e);
            throw new RuntimeException("Failed to generate employee balance report", e);
        }
    }

    // Transaction History (CSV format)
    public String generateTransactionHistoryCsv(LocalDate startDate, LocalDate endDate) {
        log.info("Starting transaction history CSV generation for period: {} to {}", startDate, endDate);

        try {
            List<Transaction> transactions = transactionRepository.findByTransactionDateBetween(startDate, endDate);
            log.debug("Found {} transactions in the specified period", transactions.size());

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

            log.info("Transaction history CSV generated successfully with {} transactions", transactions.size());
            return csv.toString();

        } catch (Exception e) {
            log.error("Error generating transaction history CSV for period {} to {}", startDate, endDate, e);
            throw new RuntimeException("Failed to generate transaction history CSV", e);
        }
    }

    // Meal Participation Report
    public MealParticipationReport generateMealParticipationReport(
            LocalDate startDate, LocalDate endDate) {

        log.info("Starting meal participation report generation for period: {} to {}", startDate, endDate);

        try {
            MealParticipationReport report = new MealParticipationReport();
            report.setStartDate(startDate);
            report.setEndDate(endDate);

            // Get meals in date range
            List<Meal> meals = mealRepository.findByMealDateBetween(startDate, endDate);
            report.setTotalMeals(meals.size());
            log.debug("Found {} meals in the specified period", meals.size());

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
            log.debug("Total participants: {}, Total cost: {}", totalParticipants, totalCost);

            // Calculate averages
            if (!meals.isEmpty()) {
                double avgParticipants = totalParticipants / (double) meals.size();
                BigDecimal avgCost = totalCost.divide(BigDecimal.valueOf(meals.size()), 2, RoundingMode.HALF_UP);

                report.setAvgParticipantsPerMeal(avgParticipants);
                report.setAvgCostPerMeal(avgCost);
                log.debug("Average participants per meal: {}, Average cost per meal: {}",
                        avgParticipants, avgCost);
            }

            // Food item statistics
            report.setMealStats(mealStats);

            log.info("Meal participation report generated successfully with {} meal types", mealStats.size());
            return report;

        } catch (Exception e) {
            log.error("Error generating meal participation report for period {} to {}", startDate, endDate, e);
            throw new RuntimeException("Failed to generate meal participation report", e);
        }
    }

    // Employee Meal History Report
    public EmployeeMealHistoryReport generateEmployeeMealHistory(String pin, int months) {
        log.info("Starting employee meal history report generation for PIN: {} for {} months", pin, months);

        try {
            Employee employee = employeeRepository.findByPin(pin)
                    .orElseThrow(() -> {
                        log.error("Employee not found with PIN: {}", pin);
                        return new RuntimeException("Employee not found");
                    });

            log.debug("Found employee: {} with PIN: {}", employee.getUsername(), pin);

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(months);

            EmployeeMealHistoryReport report = new EmployeeMealHistoryReport();
            report.setEmployeeName(employee.getUsername());
            report.setPin(pin);
            report.setPeriod(months + " months");
            report.setStartDate(startDate);
            report.setEndDate(endDate);

            List<Meal> meals = mealRepository.findMealsByEmployeeAndDateRange(employee.getId(), startDate, endDate);
            report.setTotalMeals(meals.size());
            log.debug("Found {} meals for employee {} in the specified period", meals.size(), pin);

            Map<String, Integer> mealFrequency = new HashMap<>();
            BigDecimal totalCost = BigDecimal.ZERO;

            for (Meal meal : meals) {
                String foodName = meal.getFoodItem().getName();
                mealFrequency.put(foodName, mealFrequency.getOrDefault(foodName, 0) + 1);
                totalCost = totalCost.add(meal.getPerHeadCost());
            }

            report.setTotalSpent(totalCost);
            log.debug("Total amount spent by employee {}: {}", pin, totalCost);

            log.info("Employee meal history report generated successfully for PIN: {}", pin);
            return report;

        } catch (Exception e) {
            log.error("Error generating employee meal history report for PIN: {} and {} months", pin, months, e);
            throw new RuntimeException("Failed to generate employee meal history report", e);
        }
    }

    // Low Balance Alerts
    public LowBalanceAlertReport generateLowBalanceAlerts(BigDecimal threshold) {
        log.info("Starting low balance alert report generation with threshold: {}", threshold);

        try {
            LowBalanceAlertReport report = new LowBalanceAlertReport();
            report.setThreshold(threshold);
            report.setGeneratedDate(LocalDate.now());

            List<Employee> employees = employeeRepository.findByBalanceLessThan(threshold);
            log.debug("Found {} employees with balance below threshold {}", employees.size(), threshold);

            report.setEmployees(employees.stream()
                    .map(this::convertToBalanceItem)
                    .collect(Collectors.toList()));

            log.info("Low balance alert report generated successfully with {} employees", employees.size());
            return report;

        } catch (Exception e) {
            log.error("Error generating low balance alert report with threshold: {}", threshold, e);
            throw new RuntimeException("Failed to generate low balance alert report", e);
        }
    }

    // Food Preference Report
    public FoodPreferenceReport generateFoodPreferenceReport(LocalDate startDate, LocalDate endDate) {
        log.info("Starting food preference report generation for period: {} to {}", startDate, endDate);

        try {
            FoodPreferenceReport report = new FoodPreferenceReport();
            report.setStartDate(startDate);
            report.setEndDate(endDate);

            Map<String, FoodPreferenceStat> stats = new HashMap<>();
            List<Object[]> results = foodItemRepository.getFoodPreferenceStats(startDate, endDate);
            log.debug("Retrieved food preference statistics for {} food items", results.size());

            for (Object[] result : results) {
                String foodName = (String) result[0];
                Long totalMeals = (Long) result[1];
                Long totalParticipants = (Long) result[2];

                FoodPreferenceStat stat = new FoodPreferenceStat();
                stat.setTotalMeals(totalMeals);
                stat.setTotalParticipants(totalParticipants);
                stats.put(foodName, stat);

                log.debug("Food item '{}': {} meals, {} participants", foodName, totalMeals, totalParticipants);
            }

            report.setFoodStats(stats);
            log.info("Food preference report generated successfully with {} food items", stats.size());
            return report;

        } catch (Exception e) {
            log.error("Error generating food preference report for period {} to {}", startDate, endDate, e);
            throw new RuntimeException("Failed to generate food preference report", e);
        }
    }

    // Helper: Convert Employee to DTO
    private EmployeeBalanceItem convertToBalanceItem(Employee employee) {
        log.trace("Converting employee {} to balance item", employee.getPin());
        EmployeeBalanceItem item = new EmployeeBalanceItem();
        item.setPin(employee.getPin());
        item.setName(employee.getUsername());
        item.setBalance(employee.getBalance());
        return item;
    }
}