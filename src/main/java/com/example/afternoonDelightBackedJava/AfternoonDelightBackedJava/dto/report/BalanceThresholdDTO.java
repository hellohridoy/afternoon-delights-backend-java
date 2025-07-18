package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; /**
 * DTO for balance threshold operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceThresholdDTO {

    @NotNull(message = "Threshold amount is required")
    private BigDecimal threshold;

    @NotBlank(message = "Operation type is required")
    private String operation; // "ABOVE", "BELOW", "EQUAL"

    private boolean includeInactive = false;
    private int limit = 100;

    public boolean isAboveThreshold() {
        return "ABOVE".equalsIgnoreCase(operation);
    }

    public boolean isBelowThreshold() {
        return "BELOW".equalsIgnoreCase(operation);
    }

    public boolean isEqualThreshold() {
        return "EQUAL".equalsIgnoreCase(operation);
    }
}
