package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; /**
 * DTO for balance reporting
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceReportDTO {

    private String pin;
    private String username;
    private String email;
    private String designation;
    private BigDecimal balance;
    private String balanceStatus;

    // Computed properties
    public boolean hasNegativeBalance() {
        return balance != null && balance.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean hasZeroBalance() {
        return balance != null && balance.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean hasPositiveBalance() {
        return balance != null && balance.compareTo(BigDecimal.ZERO) > 0;
    }
}
