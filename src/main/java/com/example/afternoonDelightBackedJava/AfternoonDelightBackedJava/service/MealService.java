package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Meal;

import java.time.LocalDate;
import java.util.List;

public interface MealService {

    // ✅ Get meal details for a specific date (e.g., to show what's on the menu or who participated)
    MealResponseDTO getMealByDate(LocalDate date);

    // ✅ Create a new meal entry (e.g., admin enters today's meal plan or participation list)
    Meal createMeal(MealCreationDTO mealDTO);

    // ✅ Update an existing meal by ID (used to modify meals after creation, e.g., change participants or time)
    MealResponseDTO updateMeal(Long id, MealRequestDTO mealRequestDTO);

    // ✅ Delete a meal entry by ID (used when an incorrect entry was created or needs to be removed)
    void deleteMeal(Long id);

    // ✅ Get a specific meal by ID (used for detailed view or editing by admin)
    MealResponseDTO getMealById(Long id);

    // ✅ Get all meals (used in dashboard/history view for listing past meals)
    List<MealResponseDTO> getAllMeals();

    // ✅ Toggle a participant (join/leave) for a specific meal using meal ID and employee PIN
    //    Useful when user clicks "Join" or "Cancel" in frontend
    MealResponseDTO toggleParticipant(Long mealId, String pin, boolean isParticipating);

    // ✅ Toggle participant using DTO (alternative overload accepting structured request)
    MealResponseDTO toggleParticipant(MealPinOperationDTO operation);

    // ✅ Bulk create or update meals using a list of requests (used for admin importing multiple meal entries)
    List<MealSummaryDTO> processBulkMeals(BulkMealRequestDTO bulkRequest);

    // ✅ V2: Bulk operation with more structured data per day (used for multi-day batch input or schedule)
    List<DailyMealResponseDTO> processBulkMealsV2(List<DailyMealDTO> bulkRequest);
}
