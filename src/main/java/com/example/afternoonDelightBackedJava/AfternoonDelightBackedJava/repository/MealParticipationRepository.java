package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.MealParticipation;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealParticipationRepository extends JpaRepository<MealParticipation, Long> {

    List<MealParticipation> findByMealId(Long mealId);

    Optional<MealParticipation> findByMealIdAndEmployeeId(Long mealId, Long employeeId);

    void deleteByMealIdAndEmployeeId(Long mealId, Long employeeId);

    @Query("SELECT t FROM Transaction t WHERE t.employee.pin = :pin")
    List<Transaction> findByEmployeePin(@Param("pin") String pin);

    Optional<MealParticipation> findByMealIdAndEmployeePin(Long mealId, String pin);

}
