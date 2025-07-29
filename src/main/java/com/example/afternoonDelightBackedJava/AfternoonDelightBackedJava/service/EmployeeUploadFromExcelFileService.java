package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report.EmployeeBulkImportResult;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.emums.Role;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.EmployeeRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeUploadFromExcelFileService {

    private final EmployeeRepository employeeRepository;

    public List<Employee> importEmployeesFromExcel(MultipartFile file) throws IOException {
        List<Employee> employees = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Skip header row (row 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && !isRowEmpty(row)) {
                    Employee employee = createEmployeeFromRow(row);
                    if (employee != null) {
                        employees.add(employee);
                    }
                }
            }
        }

        return saveEmployees(employees);
    }

    private Employee createEmployeeFromRow(Row row) {
        try {
            Employee employee = new Employee();

            // Column mapping (adjust based on your Excel structure)
            employee.setEmployeeId(getStringValue(row.getCell(0))); // Column A
            employee.setUsername(getStringValue(row.getCell(1))); // Column B
            employee.setEmail(getStringValue(row.getCell(2))); // Column C
            employee.setDesignation(getStringValue(row.getCell(3))); // Column D
            employee.setPhone(getStringValue(row.getCell(4))); // Column E
            employee.setBand(getStringValue(row.getCell(5))); // Column F
            employee.setAddress(getStringValue(row.getCell(6))); // Column G
            employee.setPin(getStringValue(row.getCell(7))); // Column H

            // Set default values
            employee.setBalance(BigDecimal.ZERO);
            employee.setRole(Role.EMPLOYEE);
            employee.setIsActive(true);

            // Validate required fields
            if (employee.getEmployeeId() == null ||
                    employee.getUsername() == null || employee.getUsername().trim().isEmpty() ||
                    employee.getEmail() == null || employee.getEmail().trim().isEmpty() ||
                    employee.getPin() == null || employee.getPin().trim().isEmpty()) {
                log.warn("Skipping row {} due to missing required fields", row.getRowNum());
                return null;
            }

            // Validate PIN format
            if (!employee.getPin().matches("\\d{4}")) {
                log.warn("Skipping row {} due to invalid PIN format: {}", row.getRowNum(), employee.getPin());
                return null;
            }

            return employee;

        } catch (Exception e) {
            log.error("Error processing row {}: {}", row.getRowNum(), e.getMessage());
            return null;
        }
    }

    private String getStringValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    private Long getLongValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case NUMERIC:
                return (long) cell.getNumericCellValue();
            case STRING:
                try {
                    return Long.parseLong(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < 8; i++) { // Check first 8 columns
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getStringValue(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Transactional
    protected List<Employee> saveEmployees(List<Employee> employees) {
        List<Employee> savedEmployees = new ArrayList<>();
        List<String> skippedEmployees = new ArrayList<>();

        if (employees == null || employees.isEmpty()) {
            log.info("No employees to save");
            return savedEmployees;
        }

        // Extract all usernames, emails, and pins for batch checking
        List<String> usernames = employees.stream()
                .map(Employee::getUsername)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<String> emails = employees.stream()
                .map(Employee::getEmail)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<String> pins = employees.stream()
                .map(Employee::getPin)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // Batch check for existing employees
        List<Employee> existingEmployees = employeeRepository
                .findByUsernamesOrEmailsOrPins(usernames, emails, pins);

        // Create sets for fast lookup
        Set<String> existingUsernames = existingEmployees.stream()
                .map(Employee::getUsername)
                .collect(Collectors.toSet());

        Set<String> existingEmails = existingEmployees.stream()
                .map(Employee::getEmail)
                .collect(Collectors.toSet());

        Set<String> existingPins = existingEmployees.stream()
                .map(Employee::getPin)
                .collect(Collectors.toSet());

        // Process each employee
        for (Employee employee : employees) {
            try {
                // Validate employee data
                if (!isValidEmployee(employee)) {
                    skippedEmployees.add("Invalid data: " + employee.getUsername());
                    continue;
                }

                // Check for duplicates
                if (existingUsernames.contains(employee.getUsername()) ||
                        existingEmails.contains(employee.getEmail()) ||
                        existingPins.contains(employee.getPin())) {

                    skippedEmployees.add(String.format("Duplicate: %s (username: %s, email: %s, pin: %s)",
                            employee.getUsername(), employee.getUsername(),
                            employee.getEmail(), employee.getPin()));
                    continue;
                }

                // Set default values if needed
                prepareEmployeeForSave(employee);

                // Save employee
                Employee saved = employeeRepository.save(employee);
                savedEmployees.add(saved);

                // Add to existing sets to prevent duplicates within the current batch
                existingUsernames.add(saved.getUsername());
                existingEmails.add(saved.getEmail());
                existingPins.add(saved.getPin());

                log.debug("Successfully imported employee: {}", saved.getUsername());

            } catch (DataIntegrityViolationException e) {
                log.error("Data integrity violation for employee {}: {}",
                        employee.getUsername(), e.getMessage());
                skippedEmployees.add("Data integrity error: " + employee.getUsername());
            } catch (Exception e) {
                log.error("Error saving employee {}: {}", employee.getUsername(), e.getMessage());
                skippedEmployees.add("Save error: " + employee.getUsername());
            }
        }

        // Log summary
        log.info("Employee import completed. Saved: {}, Skipped: {}",
                savedEmployees.size(), skippedEmployees.size());

        if (!skippedEmployees.isEmpty()) {
            log.warn("Skipped employees: {}", skippedEmployees);
        }

        return savedEmployees;
    }

    /**
     * Validates employee data before saving
     */
    private boolean isValidEmployee(Employee employee) {
        if (employee == null) {
            log.warn("Employee object is null");
            return false;
        }

        if (StringUtils.isBlank(employee.getUsername())) {
            log.warn("Employee username is blank");
            return false;
        }

        if (StringUtils.isBlank(employee.getEmail())) {
            log.warn("Employee email is blank for username: {}", employee.getUsername());
            return false;
        }

        if (StringUtils.isBlank(employee.getPin()) || !employee.getPin().matches("\\d{4}")) {
            log.warn("Employee PIN is invalid for username: {}. PIN must be 4 digits.",
                    employee.getUsername());
            return false;
        }

        if (!isValidEmail(employee.getEmail())) {
            log.warn("Invalid email format for username: {}", employee.getUsername());
            return false;
        }

        return true;
    }

    /**
     * Sets default values and prepares employee for saving
     */
    private void prepareEmployeeForSave(Employee employee) {
        // Set default values
        if (employee.getBalance() == null) {
            employee.setBalance(BigDecimal.ZERO);
        }

        if (employee.getRole() == null) {
            employee.setRole(Role.EMPLOYEE);
        }

        if (employee.getIsActive() == null) {
            employee.setIsActive(true);
        }

        if (StringUtils.isBlank(employee.getEmployeeId())) {
            // Generate employee ID if not provided
            employee.setEmployeeId(generateEmployeeId());
        }

        if (StringUtils.isBlank(employee.getDesignation())) {
            employee.setDesignation("Employee");
        }

        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        if (employee.getCreatedAt() == null) {
            employee.setCreatedAt(now);
        }
        employee.setUpdatedAt(now);
    }

    /**
     * Validates email format
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Generates a unique employee ID
     */
    private String generateEmployeeId() {
        // Get the count of existing employees
        long count = employeeRepository.count();
        return String.format("EMP%04d", count + 1);
    }

    /**
     * Batch save with better error handling and rollback support
     */
    @Transactional
    public EmployeeBulkImportResult bulkImportEmployees(List<Employee> employees) {
        List<Employee> savedEmployees = saveEmployees(employees);

        return EmployeeBulkImportResult.builder()
                .totalProvided(employees.size())
                .successfullyImported(savedEmployees.size())
                .skipped(employees.size() - savedEmployees.size())
                .importedEmployees(savedEmployees)
                .build();
    }}
