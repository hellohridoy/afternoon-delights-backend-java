package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    // Basic employee operations
    Optional<Employee> findByEmployeePin(String pin);
    EmployeeDTO getByUsername(String username);
    EmployeeDTO getByPin(String pin);
    EmployeeDTO createEmployee(EmployeeCreationDTO dto);
    EmployeeDTO updateEmployee(String pin, EmployeeUpdateDTO dto);
    void deactivateEmployee(String pin);
    void activateEmployee(String pin);

    // Profile picture operations
    void uploadProfilePicture(String username, MultipartFile file) throws IOException;
    void uploadProfilePictureByPin(String pin, MultipartFile file) throws IOException;
    byte[] getProfilePicture(String username);
    String getImageType(String username);
    byte[] getProfilePictureByPin(String pin);
    String getImageTypeByPin(String pin);

    // Employee listing and search
    List<EmployeeDTO> getAllEmployees();
    List<EmployeePinDTO> getAllEmployeePins(String search);

    // Balance operations - Basic
    BigDecimal getEmployeeBalance(String pin);
    void updateEmployeeBalance(String pin, BigDecimal amount);

//    // Balance operations - Range queries
//    List<EmployeeDTO> getEmployeesWithBalanceLessThanEqual(BigDecimal maxBalance);
//    List<EmployeeDTO> getEmployeesWithBalanceGreaterThanEqual(BigDecimal minBalance);
//    List<EmployeeDTO> getEmployeesWithBalanceBetween(BigDecimal minBalance, BigDecimal maxBalance);
//
//    // Balance operations - Aggregations
//    BigDecimal getTotalActiveEmployeeBalances();
//    BigDecimal getTotalAllEmployeeBalances();
//    Long getEmployeesWithNegativeBalanceCount();
//    Long getEmployeesWithZeroBalanceCount();
//
//    // Balance operations - Analytics
//    BalanceStatisticsDTO getBalanceStatistics();
//    List<EmployeeDTO> getEmployeesWithLowBalance(BigDecimal threshold);
//    List<EmployeeDTO> getEmployeesWithHighBalance(BigDecimal threshold);
//    List<EmployeeDTO> getActiveEmployeesInBalanceRange(BigDecimal minBalance, BigDecimal maxBalance);
//
//    // Balance operations - Bulk operations
//    BalanceBulkUpdateResult updateBalancesForEmployees(List<BalanceUpdateDTO> updates);
//    List<EmployeeDTO> getEmployeesRequiringBalanceAdjustment(BigDecimal minimumRequiredBalance);
//    void adjustNegativeBalancesToZero();
//
//    // Balance operations - Reporting
//    List<BalanceReportDTO> generateBalanceReport();
//
//    // Bulk import operations
//    EmployeeBulkImportResult bulkImportEmployees(List<Employee> employees);

    String getBalanceStatus(BigDecimal balance);

    List<Employee> saveEmployees(List<Employee> employees);
}