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
    @Query("SELECT mp FROM MealParticipation mp WHERE mp.meal.id = :mealId AND mp.employee.pin = :pin")
    Optional<MealParticipation> findByMealIdAndEmployeePin(
            @Param("mealId") Long mealId,
            @Param("pin") String pin
    );
    Optional<MealParticipation> findByMealIdAndEmployeeId(Long mealId, Long employeeId);

    void deleteByMealIdAndEmployeeId(Long mealId, Long employeeId);

    @Query("SELECT t FROM Transaction t WHERE t.employee.pin = :pin")
    List<Transaction> findByEmployeePin(@Param("pin") String pin);
    @Query("SELECT COUNT(p) FROM MealParticipation p WHERE p.meal.id = :mealId")
    int countByMealId(@Param("mealId") Long mealId);
    @Query("SELECT mp FROM MealParticipation mp " +
            "JOIN FETCH mp.meal m " +
            "JOIN FETCH mp.employee e " +
            "WHERE e.pin = :pin AND mp.participationDate >= :startDate " +
            "ORDER BY mp.participationDate DESC")
    List<MealParticipation> findByEmployeePinAndDateAfter(
            @Param("pin") String pin,
            @Param("startDate") LocalDate startDate
    );

    @Query("SELECT COUNT(mp) FROM MealParticipation mp " +
            "WHERE mp.employee.pin = :pin AND mp.participationDate >= :startDate")
    Long countByEmployeePinAndDateAfter(
            @Param("pin") String pin,
            @Param("startDate") LocalDate startDate
    );
}
