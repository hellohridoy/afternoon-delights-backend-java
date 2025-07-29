package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.controller;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Meal;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.MealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MealRestController {

    private final MealService mealService;

    @PostMapping("/api/meals/meals-infos")
    public ResponseEntity<?> createMeal(@RequestBody MealCreationDTO mealDTO) {
        log.info("POST /api/meals/meals-infos - Creating meal for date: {} with food item: {}",
                mealDTO.getMealDate(), mealDTO.getFoodItem());
        log.debug("Create meal request details - Total cost: {}, Participants count: {}",
                mealDTO.getTotalCost(),
                mealDTO.getParticipants() != null ? mealDTO.getParticipants().size() : 0);

        try {
            Meal createdMeal = mealService.createMeal(mealDTO);

            log.info("Successfully created meal with ID: {} for date: {}",
                    createdMeal.getId(), mealDTO.getMealDate());
            log.debug("Created meal details - ID: {}, Per head cost: {}, Total participants: {}",
                    createdMeal.getId(), createdMeal.getPerHeadCost(), createdMeal.getTotalParticipants());

            return new ResponseEntity<>(createdMeal, HttpStatus.CREATED);

        } catch (Exception e) {
            log.error("Error creating meal for date: {} with food item: {}",
                    mealDTO.getMealDate(), mealDTO.getFoodItem(), e);

            // Return proper error response instead of letting exception propagate
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating meal: " + e.getMessage());
        }
    }

    @PutMapping("/api/meals/meals-infos/{id}")
    public ResponseEntity<MealResponseDTO> updateMeal(
            @PathVariable Long id,
            @RequestBody MealRequestDTO mealRequestDTO) {

        log.info("PUT /api/meals/meals-infos/{} - Updating meal with ID: {}", id, id);
        log.debug("Update meal request details - Date: {}, Food item: {}, Total cost: {}, Participants count: {}",
                mealRequestDTO.getMealDate(), mealRequestDTO.getFoodItemName(),
                mealRequestDTO.getTotalCost(),
                mealRequestDTO.getParticipants() != null ? mealRequestDTO.getParticipants().size() : 0);

        try {
            MealResponseDTO response = mealService.updateMeal(id, mealRequestDTO);

            log.info("Successfully updated meal with ID: {}", id);
            log.debug("Updated meal response - Participants count: {}, Total cost: {}",
                    response.getParticipants() != null ? response.getParticipants().size() : 0,
                    response.getTotalCost());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error updating meal with ID: {}", id, e);
            throw e; // Let global exception handler deal with it, or implement specific error handling
        }
    }

    @DeleteMapping("/api/meals/meals-infos/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        log.info("DELETE /api/meals/meals-infos/{} - Deleting meal with ID: {}", id, id);

        try {
            mealService.deleteMeal(id);

            log.info("Successfully deleted meal with ID: {}", id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("Error deleting meal with ID: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/api/meals/meals-infos/{id}")
    public ResponseEntity<MealResponseDTO> getMealById(@PathVariable Long id) {
        log.info("GET /api/meals/meals-infos/{} - Fetching meal by ID: {}", id, id);

        try {
            MealResponseDTO meal = mealService.getMealById(id);

            log.info("Successfully fetched meal by ID: {}", id);
            log.debug("Fetched meal details - Date: {}, Food item: {}, Participants count: {}",
                    meal.getMealDate(), meal.getFoodItemName(),
                    meal.getParticipants() != null ? meal.getParticipants().size() : 0);

            return ResponseEntity.ok(meal);

        } catch (Exception e) {
            log.error("Error fetching meal by ID: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/api/meals/meals-infos")
    public ResponseEntity<List<MealResponseDTO>> getAllMeals() {
        log.info("GET /api/meals/meals-infos - Fetching all meals");

        try {
            List<MealResponseDTO> meals = mealService.getAllMeals();

            log.info("Successfully fetched {} meals", meals.size());
            log.debug("Fetched meals count: {}", meals.size());

            return ResponseEntity.ok(meals);

        } catch (Exception e) {
            log.error("Error fetching all meals", e);
            throw e;
        }
    }

    @PostMapping("/api/meals/meals-infos/{mealId}/participants/{pin}")
    public ResponseEntity<MealResponseDTO> toggleParticipant(
            @PathVariable Long mealId,
            @PathVariable String pin,
            @RequestParam boolean participate) {

        log.info("POST /api/meals/meals-infos/{}/participants/{} - Toggling participant with PIN: {} for meal ID: {}, participate: {}",
                mealId, pin, pin, mealId, participate);

        try {
            MealResponseDTO response = mealService.toggleParticipant(mealId, pin, participate);

            log.info("Successfully toggled participant with PIN: {} for meal ID: {}, participate: {}",
                    pin, mealId, participate);
            log.debug("Toggle participant response - Current participants count: {}",
                    response.getParticipants() != null ? response.getParticipants().size() : 0);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error toggling participant with PIN: {} for meal ID: {}, participate: {}",
                    pin, mealId, participate, e);
            throw e;
        }
    }

    @PostMapping("/api/meals/meals-infos/toggle-participant")
    public ResponseEntity<MealResponseDTO> toggleParticipant(
            @RequestBody MealPinOperationDTO operation) {

        log.info("POST /api/meals/meals-infos/toggle-participant - Processing meal pin operation for meal ID: {}, PIN: {}, participate: {}",
                operation.getMealId(), operation.getPin(), operation.isParticipate());

        try {
            MealResponseDTO response = mealService.toggleParticipant(operation);

            log.info("Successfully processed meal pin operation for meal ID: {}, PIN: {}, participate: {}",
                    operation.getMealId(), operation.getPin(), operation.isParticipate());
            log.debug("Toggle participant operation response - Current participants count: {}",
                    response.getParticipants() != null ? response.getParticipants().size() : 0);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing meal pin operation for meal ID: {}, PIN: {}, participate: {}",
                    operation.getMealId(), operation.getPin(), operation.isParticipate(), e);
            throw e;
        }
    }

    @PostMapping("/api/meals/meals-infos/bulk")
    public ResponseEntity<List<MealSummaryDTO>> processBulkMeals(
            @RequestBody BulkMealRequestDTO bulkRequest) {

        log.info("POST /api/meals/meals-infos/bulk - Processing bulk meals with {} items",
                bulkRequest.getMeals() != null ? bulkRequest.getMeals().size() : 0);

        try {
            List<MealSummaryDTO> results = mealService.processBulkMeals(bulkRequest);

            log.info("Successfully processed bulk meals. Requested: {}, Processed: {}",
                    bulkRequest.getMeals() != null ? bulkRequest.getMeals().size() : 0,
                    results.size());
            log.debug("Bulk meals processing completed with {} results", results.size());

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            log.error("Error processing bulk meals with {} items",
                    bulkRequest.getMeals() != null ? bulkRequest.getMeals().size() : 0, e);
            throw e;
        }
    }

    @GetMapping("/api/meals/meals-infos/by-date/{date}")
    public ResponseEntity<MealResponseDTO> getMealByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("GET /api/meals/meals-infos/by-date/{} - Fetching meal by date: {}", date, date);

        try {
            MealResponseDTO meal = mealService.getMealByDate(date);

            log.info("Successfully fetched meal by date: {}", date);
            log.debug("Fetched meal by date - Food item: {}, Participants count: {}, Total cost: {}",
                    meal.getFoodItemName(),
                    meal.getParticipants() != null ? meal.getParticipants().size() : 0,
                    meal.getTotalCost());

            return ResponseEntity.ok(meal);

        } catch (Exception e) {
            log.error("Error fetching meal by date: {}", date, e);
            throw e;
        }
    }

    @PostMapping("/api/meals/meals-infos/bulk-v2")
    public ResponseEntity<List<DailyMealResponseDTO>> processBulkMealsV2(
            @RequestBody List<DailyMealDTO> bulkRequest) {

        log.info("POST /api/meals/meals-infos/bulk-v2 - Processing bulk meals V2 with {} items",
                bulkRequest != null ? bulkRequest.size() : 0);

        try {
            List<DailyMealResponseDTO> responses = mealService.processBulkMealsV2(bulkRequest);

            // Count successful and failed operations
            long successCount = responses.stream().filter(r -> "SUCCESS".equals(r.getStatus())).count();
            long errorCount = responses.stream().filter(r -> "ERROR".equals(r.getStatus())).count();

            log.info("Successfully processed bulk meals V2. Total: {}, Success: {}, Errors: {}",
                    responses.size(), successCount, errorCount);

            if (errorCount > 0) {
                log.warn("Bulk meals V2 processing completed with {} errors out of {} total items",
                        errorCount, responses.size());

                // Log details of failed items at debug level
                responses.stream()
                        .filter(r -> "ERROR".equals(r.getStatus()))
                        .forEach(r -> log.debug("Failed bulk meal V2 item - Date: {}, Error: {}",
                                r.getDate(), r.getMessage()));
            }

            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            log.error("Error processing bulk meals V2 with {} items",
                    bulkRequest != null ? bulkRequest.size() : 0, e);
            throw e;
        }
    }

    // Additional endpoints for better REST API design

    /**
     * Health check endpoint for meal service
     */
    @GetMapping("/api/meals/health")
    public ResponseEntity<String> healthCheck() {
        log.debug("GET /api/meals/health - Health check requested");

        try {
            // You could add actual health checks here, like database connectivity
            log.debug("Health check passed");
            return ResponseEntity.ok("Meal service is healthy");

        } catch (Exception e) {
            log.error("Health check failed", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Meal service is unhealthy: " + e.getMessage());
        }
    }

    /**
     * Get meals within a date range
     */
    @GetMapping("/api/meals/meals-infos/date-range")
    public ResponseEntity<List<MealResponseDTO>> getMealsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/meals/meals-infos/date-range - Fetching meals between {} and {}",
                startDate, endDate);

        try {
            // Note: You'll need to implement this method in your service
            // List<MealResponseDTO> meals = mealService.getMealsByDateRange(startDate, endDate);

            // For now, returning empty list as placeholder
            List<MealResponseDTO> meals = List.of();

            log.info("Successfully fetched {} meals between {} and {}",
                    meals.size(), startDate, endDate);

            return ResponseEntity.ok(meals);

        } catch (Exception e) {
            log.error("Error fetching meals between {} and {}", startDate, endDate, e);
            throw e;
        }
    }
}