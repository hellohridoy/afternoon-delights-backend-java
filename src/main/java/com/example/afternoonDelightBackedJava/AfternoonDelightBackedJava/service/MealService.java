package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface MealService {
    MealResponseDTO getMealByDate(LocalDate date); // <-- Add this

    MealResponseDTO createMeal(MealRequestDTO mealRequestDTO);
    MealResponseDTO updateMeal(Long id, MealRequestDTO mealRequestDTO);
    void deleteMeal(Long id);
    MealResponseDTO getMealById(Long id);
    List<MealResponseDTO> getAllMeals();
    MealResponseDTO toggleParticipant(Long mealId, String pin, boolean isParticipating);
    MealResponseDTO toggleParticipant(MealPinOperationDTO operation);
    List<MealSummaryDTO> processBulkMeals(BulkMealRequestDTO bulkRequest);
    public Long processDailyMeal(DailyMealDTO dailyMeal);
    List<DailyMealResponseDTO> processBulkMealsV2(List<DailyMealDTO> bulkRequest);
}
