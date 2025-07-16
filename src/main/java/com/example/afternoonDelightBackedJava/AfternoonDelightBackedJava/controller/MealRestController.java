package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.controller;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.MealService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MealRestController {

    
    private final MealService mealService;

    @PostMapping("/api/meals/meals-infos")
    public ResponseEntity<MealResponseDTO> createMeal(@RequestBody MealRequestDTO mealRequestDTO) {
        MealResponseDTO response = mealService.createMeal(mealRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/api/meals/meals-infos/{id}")
    public ResponseEntity<MealResponseDTO> updateMeal(
            @PathVariable Long id,
            @RequestBody MealRequestDTO mealRequestDTO) {
        MealResponseDTO response = mealService.updateMeal(id, mealRequestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/meals/meals-infos/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        mealService.deleteMeal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/meals/meals-infos/{id}")
    public ResponseEntity<MealResponseDTO> getMealById(@PathVariable Long id) {
        return ResponseEntity.ok(mealService.getMealById(id));
    }

    @GetMapping("/api/meals/meals-infos")
    public ResponseEntity<List<MealResponseDTO>> getAllMeals() {
        return ResponseEntity.ok(mealService.getAllMeals());
    }

    @PostMapping("/api/meals/meals-infos/{mealId}/participants/{pin}")
    public ResponseEntity<MealResponseDTO> toggleParticipant(
            @PathVariable Long mealId,
            @PathVariable String pin,
            @RequestParam boolean participate) {
        MealResponseDTO response = mealService.toggleParticipant(mealId, pin, participate);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/meals/meals-infos/toggle-participant")
    public ResponseEntity<MealResponseDTO> toggleParticipant(
            @RequestBody MealPinOperationDTO operation) {
        MealResponseDTO response = mealService.toggleParticipant(operation);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/meals/meals-infos/bulk")
    public ResponseEntity<List<MealSummaryDTO>> processBulkMeals(
            @RequestBody BulkMealRequestDTO bulkRequest) {
        List<MealSummaryDTO> results = mealService.processBulkMeals(bulkRequest);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/api/meals/meals-infos/{date}")
    public ResponseEntity<MealResponseDTO> getMealByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        MealResponseDTO meal = mealService.getMealByDate(date);
        return ResponseEntity.ok(meal);
    }

    @PostMapping("/api/meals/meals-infos/bulk-v2")
    public ResponseEntity<List<DailyMealResponseDTO>> processBulkMealsV2(
            @RequestBody List<DailyMealDTO> bulkRequest) {
        List<DailyMealResponseDTO> responses = mealService.processBulkMealsV2(bulkRequest);
        return ResponseEntity.ok(responses);
    }
}
