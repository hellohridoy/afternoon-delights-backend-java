package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Meal;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.MealParticipation;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealParticipationRepository extends JpaRepository<MealParticipation, Long> {

    List<MealParticipation> findByMealId(Long mealId);

    Optional<MealParticipation> findByMealIdAndEmployeeId(Long mealId, Long employeeId);

    void deleteByMealIdAndEmployeeId(Long mealId, Long employeeId);

    @Query("SELECT t FROM Transaction t WHERE t.employee.pin = :pin")
    List<Transaction> findByEmployeePin(@Param("pin") String pin);
    @Query("SELECT COUNT(p) FROM MealParticipation p WHERE p.meal.id = :mealId")
    int countByMealId(@Param("mealId") Long mealId);
    Optional<MealParticipation> findByMealIdAndEmployeePin(Long mealId, String pin);
    @Query("SELECT p.meal FROM MealParticipation p " +
            "WHERE p.employee.id = :employeeId " +
            "AND p.meal.date BETWEEN :start AND :end")
    List<Meal> findMealsByEmployeeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
