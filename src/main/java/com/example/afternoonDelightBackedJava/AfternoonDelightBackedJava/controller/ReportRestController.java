package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.controller;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class ReportRestController {
    
    private final ReportService reportService;

    @GetMapping("/api/reports/employee-balance")
    public ResponseEntity<EmployeeBalanceReport> generateEmployeeBalanceReport(
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance) {
        return ResponseEntity.ok(reportService.generateEmployeeBalanceReport(minBalance, maxBalance));
    }

    @GetMapping(value = "/api/reports/transaction-history", produces = "text/csv")
    public ResponseEntity<String> generateTransactionHistoryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=transaction-history.csv")
                .body(reportService.generateTransactionHistoryCsv(startDate, endDate));
    }


    @GetMapping("/api/reports/meal-participation")
    public ResponseEntity<MealParticipationReport> generateMealParticipationReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.generateMealParticipationReport(startDate, endDate));
    }

    // Employee Meal History
    @GetMapping("/api/reports/employee-meal-history/{pin}")
    public ResponseEntity<EmployeeMealHistoryReport> generateEmployeeMealHistory(
            @PathVariable String pin,
            @RequestParam int months) {
        return ResponseEntity.ok(reportService.generateEmployeeMealHistory(pin, months));
    }

    // Low Balance Alert Report
    @GetMapping("/api/reports/low-balance-alerts")
    public ResponseEntity<LowBalanceAlertReport> generateLowBalanceAlerts(
            @RequestParam(defaultValue = "500") BigDecimal threshold) {
        return ResponseEntity.ok(reportService.generateLowBalanceAlerts(threshold));
    }

    // Food Preference Report
    @GetMapping("/api/reports/food-preferences")
    public ResponseEntity<LowBalanceAlertReport> generateFoodPreferenceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam BigDecimal lowBalance) {
        return ResponseEntity.ok(reportService.generateLowBalanceAlerts(lowBalance));
    }
}
