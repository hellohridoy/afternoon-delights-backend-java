package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for employee PIN information used in dropdowns and selection lists
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePinDTO {

    private Long id;
    private String pin;
    private String username;
    private String designation;
    private String email;
    private BigDecimal balance;

    // Constructor matching the repository query (id, pin, email, balance)
    public EmployeePinDTO(Long id, String pin, String email, BigDecimal balance) {
        this.id = id;
        this.pin = pin;
        this.email = email;
        this.balance = balance;
    }

    // Constructor for display purposes (pin, username, designation, email)
    public EmployeePinDTO(String pin, String username, String designation, String email) {
        this.pin = pin;
        this.username = username;
        this.designation = designation;
        this.email = email;
    }
}