package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class FoodPreferenceReport {
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, FoodPreferenceStat> foodStats;
}
