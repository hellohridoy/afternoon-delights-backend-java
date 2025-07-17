package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.emums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long employeeId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    // Add to Employee.java
    @Column(name = "balance", precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private Role role = Role.EMPLOYEE;

    private String designation;

    private String phone;
    
    private String band;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Lob
    @Column(name = "profile_picture")
    private byte[] profilePicture;

    @Column(name = "image_type")
    private String imageType;
    
    private String address;

    @Column(unique = true, nullable = false)
    @Size(min = 4, max = 4)
    @Pattern(regexp = "\\d{4}", message = "PIN must be exactly 4 digits")
    private String pin;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MealParticipation> mealParticipationList;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MealRequest> mealRequests;

}
