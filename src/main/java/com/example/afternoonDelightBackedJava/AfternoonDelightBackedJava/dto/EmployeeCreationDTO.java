package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.emums.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating new employees
 * Contains all required fields for employee registration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCreationDTO {

    @NotBlank(message = "Employee ID cannot be blank")
    private String employeeId;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3-50 characters")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "PIN cannot be blank")
    @Size(min = 4, max = 4, message = "PIN must be exactly 4 digits")
    @Pattern(regexp = "\\d{4}", message = "PIN must contain only digits")
    private String pin;

    @NotBlank(message = "Designation cannot be blank")
    private String designation;

    @Pattern(regexp = "\\+?[0-9\\s-]{10,15}", message = "Invalid phone number format")
    private String phone;

    private String band;

    private String address;

    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Builder.Default
    private Role role = Role.EMPLOYEE;

    // Optional: Manager assignment
    private String managerPin;

    // Optional: Profile picture data
    private byte[] profilePicture;

    private String imageType;
}