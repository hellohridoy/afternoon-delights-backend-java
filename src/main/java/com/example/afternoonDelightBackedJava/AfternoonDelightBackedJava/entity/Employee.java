package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.emums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "employees",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_employee_pin", columnNames = "pin"),
                @UniqueConstraint(name = "uk_employee_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_employee_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_employee_id", columnNames = "employee_id")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Business identifier (employee ID from company system)
    @Column(name = "employee_id", nullable = false, unique = true)
    @NotBlank(message = "Employee ID cannot be blank")
    private String employeeId;

    @Column(nullable = false, unique = true)
    @Size(min = 3, max = 50, message = "Username must be between 3-50 characters")
    private String username;

    @Column(nullable = false, unique = true)
    @Email(message = "Email should be valid")
    private String email;

    @Column(precision = 10, scale = 2, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.EMPLOYEE;

    @NotBlank(message = "Designation cannot be blank")
    private String designation;

    @Pattern(regexp = "\\+?[0-9\\s-]{10,15}", message = "Invalid phone number format")
    private String phone;

    private String band;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Lob
    @Column(name = "profile_picture")
    private byte[] profilePicture;

    @Column(name = "image_type", length = 50)
    private String imageType;

    private String address;

    @Column(nullable = false, unique = true)
    @Size(min = 4, max = 4, message = "PIN must be exactly 4 digits")
    @Pattern(regexp = "\\d{4}", message = "PIN must contain only digits")
    private String pin;

    // Self-referencing relationship for manager/supervisor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = true)
    private Employee manager;

    // Relationships
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealParticipation> mealParticipationList;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealRequest> mealRequests;

    // If this employee is a manager, they can have subordinates
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    private List<Employee> subordinates;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Business key equality check - using employeeId and email as business identifiers
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(employeeId, employee.employeeId) &&
                Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, email);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", employeeId='" + employeeId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", designation='" + designation + '\'' +
                ", pin='" + pin + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}