package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.FoodPreferenceStatsDTO;
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

    // Add @Query annotation to prevent Spring from trying to parse this as a derived query
    @Query("SELECT f.name as foodName, COUNT(m) as mealCount " +
            "FROM FoodItem f " +
            "JOIN f.meals m " +
            "WHERE m.mealDate BETWEEN :startDate AND :endDate " +
            "GROUP BY f.id, f.name " +
            "ORDER BY COUNT(m) DESC")
    List<Object[]> getFoodPreferenceStats(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    // Alternative: Return a proper DTO instead of Object[]
    @Query("SELECT new com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.FoodPreferenceStatsDTO(" +
            "f.name, COUNT(m)) " +
            "FROM FoodItem f " +
            "JOIN f.meals m " +
            "WHERE m.mealDate BETWEEN :startDate AND :endDate " +
            "GROUP BY f.id, f.name " +
            "ORDER BY COUNT(m) DESC")
    List<FoodPreferenceStatsDTO> getFoodPreferenceStatsDTO(@Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);
}