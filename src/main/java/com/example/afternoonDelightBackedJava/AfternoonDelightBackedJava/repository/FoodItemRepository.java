package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    Optional<FoodItem> findByName(String name);
    @Query("SELECT fi.name, COUNT(m), SUM(p.id) " +
            "FROM Meal m " +
            "JOIN m.foodItem fi " +
            "JOIN MealParticipation p ON p.meal.id = m.id " +
            "WHERE m.date BETWEEN :start AND :end " +
            "GROUP BY fi.name")
    List<Object[]> getFoodPreferenceStats(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
