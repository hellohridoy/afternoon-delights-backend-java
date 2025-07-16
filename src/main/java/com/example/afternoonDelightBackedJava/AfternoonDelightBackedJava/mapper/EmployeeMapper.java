package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.mapper;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeePersonalInfoDto;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;

public class EmployeeMapper {

    public static EmployeePersonalInfoDto toDto(Employee employee) {
        EmployeePersonalInfoDto dto = new EmployeePersonalInfoDto();
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
        dto.setRole(employee.getRole().name());
        return dto;
    }

    public static Employee toEntity(EmployeePersonalInfoDto dto) {
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
        return employee;
    }
}