package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meals")
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate mealDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id")
    private FoodItem foodItem;

    @Column(name = "per_head_cost", precision = 10, scale = 2)
    private BigDecimal perHeadCost;

    @Column(name = "total_participants")
    private Integer totalParticipants = 0;

    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "is_total_cost_fixed")
    private Boolean isTotalCostFixed = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Remove the duplicate 'date' field - use mealDate consistently
    // @Column(name = "date")  // Remove this duplicate field
    // private LocalDate date;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MealParticipation> mealParticipationList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Employee createdBy;

    public Meal(LocalDate mealDate, FoodItem foodItem, BigDecimal perHeadCost) {
        this.mealDate = mealDate;
        this.foodItem = foodItem;
        this.perHeadCost = perHeadCost;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Custom setters that update the timestamp
    public void setPerHeadCost(BigDecimal perHeadCost) {
        this.perHeadCost = perHeadCost;
        this.updatedAt = LocalDateTime.now();
    }

    public void setTotalParticipants(Integer totalParticipants) {
        this.totalParticipants = totalParticipants;
        this.updatedAt = LocalDateTime.now();
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
        this.updatedAt = LocalDateTime.now();
    }

    // Convenience method to get participations
    public List<MealParticipation> getParticipations() {
        return mealParticipationList;
    }

    public void setParticipations(List<MealParticipation> participations) {
        this.mealParticipationList = participations;
    }
}