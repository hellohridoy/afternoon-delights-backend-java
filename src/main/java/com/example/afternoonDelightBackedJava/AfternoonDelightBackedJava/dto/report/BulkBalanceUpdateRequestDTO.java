package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List; /**
 * Request DTO for bulk balance updates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkBalanceUpdateRequestDTO {

    @NotNull(message = "Balance updates list is required")
    private List<BalanceUpdateDTO> updates;

    private String batchDescription;
    private boolean validateBalances = true;
    private boolean allowNegativeBalances = false;

    public int getTotalUpdates() {
        return updates != null ? updates.size() : 0;
    }

    public BigDecimal getTotalAmount() {
        if (updates == null) return BigDecimal.ZERO;
        return updates.stream()
                .map(BalanceUpdateDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
