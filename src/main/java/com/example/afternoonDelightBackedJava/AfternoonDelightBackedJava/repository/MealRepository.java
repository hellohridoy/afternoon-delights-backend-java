package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    // Fixed query - use mealDate instead of date
    @Query("SELECT p.meal FROM MealParticipation p WHERE p.employee.id = :employeeId AND p.meal.mealDate BETWEEN :start AND :end")
    List<Meal> findMealsByEmployeeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // Alternative method using participationDate if that's what you want
    @Query("SELECT DISTINCT p.meal FROM MealParticipation p WHERE p.employee.id = :employeeId AND p.participationDate BETWEEN :start AND :end")
    List<Meal> findMealsByEmployeeAndParticipationDateRange(
            @Param("employeeId") Long employeeId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // Method to find meals with their participations
    @Query("SELECT m FROM Meal m LEFT JOIN FETCH m.mealParticipationList mp " +
            "LEFT JOIN FETCH mp.employee e " +
            "WHERE e.pin = :pin AND m.mealDate >= :startDate")
    List<Meal> findMealsWithParticipationsByEmployeePin(
            @Param("pin") String pin,
            @Param("startDate") LocalDate startDate
    );

    @Query("SELECT m FROM Meal m LEFT JOIN FETCH m.mealParticipationList " +
            "WHERE m.id = :mealId")
    Optional<Meal> findByIdWithParticipations(@Param("mealId") Long mealId);

    // Find meals by date range
    @Query("SELECT m FROM Meal m WHERE m.mealDate BETWEEN :startDate AND :endDate ORDER BY m.mealDate DESC")
    List<Meal> findByMealDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find meals by specific date
    // In your MealRepository interface, add this method:

    /**
     * Find a meal by meal date
     * @param mealDate the date to search for
     * @return Optional containing the meal if found, empty otherwise
     */

    // Alternative: If you want to fetch with participations eagerly loaded
    @Query("SELECT m FROM Meal m LEFT JOIN FETCH m.mealParticipationList mp " +
            "LEFT JOIN FETCH mp.employee " +
            "WHERE m.mealDate = :mealDate")
    Optional<Meal> findByMealDateWithParticipations(@Param("mealDate") LocalDate mealDate);

    // Add these methods to your MealRepository interface

    // This should return Optional<Meal> to handle cases where no meal exists
    Optional<Meal> findByMealDate(LocalDate mealDate);

    // Alternative: if you want to return the first meal found for a date
    @Query("SELECT m FROM Meal m WHERE m.mealDate = :mealDate ORDER BY m.id ASC")
    Optional<Meal> findFirstByMealDate(@Param("mealDate") LocalDate mealDate);
}