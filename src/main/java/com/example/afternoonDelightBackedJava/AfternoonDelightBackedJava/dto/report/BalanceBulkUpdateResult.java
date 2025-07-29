package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List; /**
 * DTO for bulk balance update results
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceBulkUpdateResult {

    private int totalRequested;
    private int successful;
    private int failed;
    private List<String> successfulPins;
    private List<String> failedPins;
    private BigDecimal totalAmountProcessed;

    // Computed properties
    public double getSuccessRate() {
        if (totalRequested == 0) return 0.0;
        return (double) successful / totalRequested * 100.0;
    }

    public boolean isCompleteSuccess() {
        return failed == 0;
    }

    public String getSummary() {
        return String.format("Bulk update completed: %d/%d successful (%.1f%%), Amount processed: %s",
                successful, totalRequested, getSuccessRate(), totalAmountProcessed);
    }
}
