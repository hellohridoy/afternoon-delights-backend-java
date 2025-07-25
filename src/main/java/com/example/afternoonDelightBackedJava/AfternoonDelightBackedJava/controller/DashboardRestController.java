package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.controller;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.DashboardSummaryDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DashboardRestController {

    private final DashboardService dashboardService;

    @GetMapping("/api/v1/dashboard/summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
        log.info("Received request to get dashboard summary");

        try {
            DashboardSummaryDTO summary = dashboardService.getDashboardSummary();
            log.info("Successfully retrieved dashboard summary");
            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            log.error("Error occurred while fetching dashboard summary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}