package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class MealRequestResponseDTO {
    private Long id;
    private LocalDate mealDate;
    private String foodItemName;
    private BigDecimal perHeadCost;
    private BigDecimal totalCost;
    private int totalParticipants;
    private List<String> participants;
    private List<TransactionResponseDTO> transactions;
}