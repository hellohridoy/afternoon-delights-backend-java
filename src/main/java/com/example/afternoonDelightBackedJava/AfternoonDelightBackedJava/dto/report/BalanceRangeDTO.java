package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; /**
 * DTO for balance range queries
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceRangeDTO {

    @NotNull(message = "Minimum balance is required")
    private BigDecimal minBalance;

    @NotNull(message = "Maximum balance is required")
    private BigDecimal maxBalance;

    private boolean includeInactive = false;
    private String sortBy = "balance";
    private String sortDirection = "DESC";

    // Validation method
    public boolean isValidRange() {
        return minBalance != null && maxBalance != null &&
                minBalance.compareTo(maxBalance) <= 0;
    }
}
