package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.controller;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportRestController {

    private final ReportService reportService;

    @GetMapping("/api/v1/reports/employee-balance")
    public ResponseEntity<EmployeeBalanceReport> generateEmployeeBalanceReport(
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance) {

        log.info("Received request for employee balance report with minBalance: {} and maxBalance: {}",
                minBalance, maxBalance);

        try {
            EmployeeBalanceReport report = reportService.generateEmployeeBalanceReport(minBalance, maxBalance);
            log.info("Employee balance report generated successfully with {} employees",
                    report.getEmployees().size());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Error generating employee balance report", e);
            throw e;
        }
    }

    @GetMapping(value = "/api/v1/reports/transaction-history", produces = "text/csv")
    public ResponseEntity<String> generateTransactionHistoryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Received request for transaction history CSV report for period: {} to {}",
                startDate, endDate);

        try {
            String csvContent = reportService.generateTransactionHistoryCsv(startDate, endDate);
            log.info("Transaction history CSV report generated successfully");
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=transaction-history.csv")
                    .body(csvContent);
        } catch (Exception e) {
            log.error("Error generating transaction history CSV report for period {} to {}",
                    startDate, endDate, e);
            throw e;
        }
    }

    @GetMapping("/api/v1/reports/meal-participation")
    public ResponseEntity<MealParticipationReport> generateMealParticipationReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Received request for meal participation report for period: {} to {}",
                startDate, endDate);

        try {
            MealParticipationReport report = reportService.generateMealParticipationReport(startDate, endDate);
            log.info("Meal participation report generated successfully with {} total meals",
                    report.getTotalMeals());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Error generating meal participation report for period {} to {}",
                    startDate, endDate, e);
            throw e;
        }
    }

    @GetMapping("/api/v1/reports/employee-meal-history/{pin}")
    public ResponseEntity<EmployeeMealHistoryReport> generateEmployeeMealHistory(
            @PathVariable String pin,
            @RequestParam int months) {

        log.info("Received request for employee meal history report for PIN: {} for {} months",
                pin, months);

        try {
            EmployeeMealHistoryReport report = reportService.generateEmployeeMealHistory(pin, months);
            log.info("Employee meal history report generated successfully for PIN: {} with {} total meals",
                    pin, report.getTotalMeals());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Error generating employee meal history report for PIN: {} and {} months",
                    pin, months, e);
            throw e;
        }
    }

    @GetMapping("/api/v1/reports/low-balance-alerts")
    public ResponseEntity<LowBalanceAlertReport> generateLowBalanceAlerts(
            @RequestParam(defaultValue = "500") BigDecimal threshold) {

        log.info("Received request for low balance alerts report with threshold: {}", threshold);

        try {
            LowBalanceAlertReport report = reportService.generateLowBalanceAlerts(threshold);
            log.info("Low balance alerts report generated successfully with {} employees below threshold",
                    report.getEmployees().size());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Error generating low balance alerts report with threshold: {}", threshold, e);
            throw e;
        }
    }

    @GetMapping("/api/v1/reports/food-preferences")
    public ResponseEntity<FoodPreferenceReport> generateFoodPreferenceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Received request for food preference report for period: {} to {}",
                startDate, endDate);

        try {
            FoodPreferenceReport report = reportService.generateFoodPreferenceReport(startDate, endDate);
            log.info("Food preference report generated successfully with {} food items",
                    report.getFoodStats().size());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Error generating food preference report for period {} to {}",
                    startDate, endDate, e);
            throw e;
        }
    }
}