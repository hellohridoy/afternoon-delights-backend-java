package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
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

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MealParticipation> mealParticipationList;

    // Constructors
    public Meal() {}

    public Meal(LocalDate mealDate, FoodItem foodItem, BigDecimal perHeadCost) {
        this.mealDate = mealDate;
        this.foodItem = foodItem;
        this.perHeadCost = perHeadCost;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getMealDate() { return mealDate; }
    public void setMealDate(LocalDate mealDate) { this.mealDate = mealDate; }

    public FoodItem getFoodItem() { return foodItem; }
    public void setFoodItem(FoodItem foodItem) { this.foodItem = foodItem; }

    public BigDecimal getPerHeadCost() { return perHeadCost; }
    public void setPerHeadCost(BigDecimal perHeadCost) {
        this.perHeadCost = perHeadCost;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getTotalParticipants() { return totalParticipants; }
    public void setTotalParticipants(Integer totalParticipants) {
        this.totalParticipants = totalParticipants;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getIsTotalCostFixed() { return isTotalCostFixed; }
    public void setIsTotalCostFixed(Boolean isTotalCostFixed) { this.isTotalCostFixed = isTotalCostFixed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

    public List<MealParticipation> getParticipations() { return mealParticipationList; }
    public void setParticipations(List<MealParticipation> participations) { this.mealParticipationList = participations; }
}
