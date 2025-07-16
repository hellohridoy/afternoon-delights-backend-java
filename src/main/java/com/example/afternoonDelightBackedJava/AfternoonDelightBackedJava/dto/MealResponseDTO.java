package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class MealResponseDTO {
    private Long id;
    private LocalDate mealDate;
    private List<String> Participants;
    private String foodItemName;
    private BigDecimal perHeadCost;
    private Integer totalParticipants;
     private List<TransactionResponseDTO> transactions;
    private BigDecimal totalCost;
    private Boolean userParticipated;
    private Boolean isTotalCostFixed;
}
