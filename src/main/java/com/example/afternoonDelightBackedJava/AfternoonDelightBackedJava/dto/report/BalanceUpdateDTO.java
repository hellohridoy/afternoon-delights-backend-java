package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; /**
 * DTO for individual balance update operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceUpdateDTO {

    @NotBlank(message = "Employee PIN is required")
    private String pin;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    private String description;
    private String reason;

    // Validation methods
    public boolean isDebit() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isCredit() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
