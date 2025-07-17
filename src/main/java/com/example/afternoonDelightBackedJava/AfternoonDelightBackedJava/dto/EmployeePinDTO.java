package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePinDTO {
    private Long id;
    private String pin;
    private String email;
    private BigDecimal balance;
}
