package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.FoodItem;
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
    List<Meal> findByMealDateBetween(LocalDate startDate, LocalDate endDate);

    List<Meal> findByMealDateAndFoodItem(LocalDate date, FoodItem foodItem);

    List<Meal> findByMealDateOrderByCreatedAtDesc(LocalDate mealDate);

    Optional<Meal> findByMealDate(LocalDate date);

    boolean existsByMealDate(LocalDate date);

    @Query("SELECT m FROM Meal m WHERE YEAR(m.mealDate) = :year AND MONTH(m.mealDate) = :month ORDER BY m.mealDate DESC")
    List<Meal> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT m FROM Meal m WHERE m.mealDate >= :startDate ORDER BY m.mealDate DESC")
    List<Meal> findRecentMeals(@Param("startDate") LocalDate startDate);
}