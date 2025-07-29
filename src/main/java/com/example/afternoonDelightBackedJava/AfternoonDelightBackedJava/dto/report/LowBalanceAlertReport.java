package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class LowBalanceAlertReport {
    private BigDecimal threshold;
    private LocalDate generatedDate;
    private List<EmployeeBalanceItem> employees;
}
