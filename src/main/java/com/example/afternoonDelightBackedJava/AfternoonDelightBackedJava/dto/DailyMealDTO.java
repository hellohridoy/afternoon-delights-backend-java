package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMealDTO {
    private LocalDate date;
    private String foodItemName;
    private BigDecimal totalCost;
    private List<String> participants;
}

