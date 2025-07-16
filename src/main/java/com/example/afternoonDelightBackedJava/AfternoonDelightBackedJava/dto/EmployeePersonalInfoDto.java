package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePersonalInfoDto {
    private Long id;
    private Long employeeId;
    private String username;
    private String email;
    private String designation;
    private String phone;
    private String band;
    private Boolean isActive;
    private String address;
    private String pin;
    private String role;
}