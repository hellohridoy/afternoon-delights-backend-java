package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Meal;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.MealParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealParticipationRepository extends JpaRepository<MealParticipation, Long> {

    List<MealParticipation> findByMealId(Long mealId);

    Optional<MealParticipation> findByMealIdAndEmployeeId(Long mealId, Long employeeId);

    void deleteByMealIdAndEmployeeId(Long mealId, Long employeeId);
    
    Optional<MealParticipation> findByMealIdAndEmployeePin(Long mealId, String pin);

}
