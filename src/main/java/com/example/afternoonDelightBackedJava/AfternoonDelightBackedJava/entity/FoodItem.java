package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "food_items")
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;
    
    private LocalDateTime mealDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "foodItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Meal> meals;

    @OneToMany(mappedBy = "foodItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MealRequest> mealRequests;
}
