package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.mapper;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeeDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.emums.Role;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;

public class EmployeeMapper {

    public static EmployeeDTO toDto(Employee employee) {
        if (employee == null) return null;

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setUsername(employee.getUsername());
        dto.setEmail(employee.getEmail());
        dto.setDesignation(employee.getDesignation());
        dto.setPhone(employee.getPhone());
        dto.setBand(employee.getBand());
        dto.setIsActive(employee.getIsActive());
        dto.setAddress(employee.getAddress());
        dto.setPin(employee.getPin());

        // Handle role conversion safely
        if (employee.getRole() != null) {
            dto.setRole(employee.getRole().name());
        } else {
            dto.setRole(Role.EMPLOYEE.name()); // Default role
        }

        return dto;
    }

    public static Employee toEntity(EmployeeDTO dto) {
        if (dto == null) return null;

        Employee employee = new Employee();
        employee.setId(dto.getId());
        employee.setEmployeeId(dto.getEmployeeId());
        employee.setUsername(dto.getUsername());
        employee.setEmail(dto.getEmail());
        employee.setDesignation(dto.getDesignation());
        employee.setPhone(dto.getPhone());
        employee.setBand(dto.getBand());
        employee.setIsActive(dto.getIsActive());
        employee.setAddress(dto.getAddress());
        employee.setPin(dto.getPin());

        // Convert string role to enum
        if (dto.getRole() != null) {
            employee.setRole(Role.valueOf(dto.getRole()));
        } else {
            employee.setRole(Role.EMPLOYEE); // Default role
        }

        return employee;
    }
}