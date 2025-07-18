package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.emums.Role;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public Optional<Employee> findByEmployeePin(String pin) {
        return employeeRepository.findByPin(pin);
    }

    @Override
    public EmployeeDTO getByUsername(String username) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with username: " + username));
        return convertToDTO(employee);
    }

    @Override
    public EmployeeDTO getByPin(String pin) {
        Employee employee = employeeRepository.findByPin(pin)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with PIN: " + pin));
        return convertToDTO(employee);
    }

    @Override
    public void uploadProfilePicture(String username, MultipartFile file) throws IOException {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with username: " + username));

        // Validate file type
        validateImageFile(file);

        employee.setProfilePicture(file.getBytes());
        employee.setImageType(file.getContentType());
        employeeRepository.save(employee);
    }

    @Override
    public void uploadProfilePictureByPin(String pin, MultipartFile file) throws IOException {
        Employee employee = employeeRepository.findByPin(pin)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with PIN: " + pin));

        // Validate file type
        validateImageFile(file);

        employee.setProfilePicture(file.getBytes());
        employee.setImageType(file.getContentType());
        employeeRepository.save(employee);
    }

    @Override
    public byte[] getProfilePicture(String username) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with username: " + username));
        return employee.getProfilePicture();
    }

    @Override
    public String getImageType(String username) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with username: " + username));
        return employee.getImageType();
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO createEmployee(EmployeeCreationDTO dto) {
        // Check if employee already exists
        if (employeeRepository.findByPin(dto.getPin()).isPresent()) {
            throw new RuntimeException("Employee with PIN " + dto.getPin() + " already exists");
        }
        if (employeeRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Employee with email " + dto.getEmail() + " already exists");
        }
        if (employeeRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Employee with username " + dto.getUsername() + " already exists");
        }

        Employee employee = new Employee();
        employee.setEmployeeId(dto.getEmployeeId());
        employee.setUsername(dto.getUsername());
        employee.setEmail(dto.getEmail());
        employee.setPin(dto.getPin());
        employee.setDesignation(dto.getDesignation());
        employee.setPhone(dto.getPhone());
        employee.setBand(dto.getBand());
        employee.setAddress(dto.getAddress());
        employee.setBalance(dto.getBalance() != null ? dto.getBalance() : BigDecimal.ZERO);
        employee.setRole(dto.getRole());
        employee.setIsActive(true);

        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDTO(savedEmployee);
    }

    @Override
    public List<EmployeePinDTO> getAllEmployeePins(String search) {
        if (search != null && !search.trim().isEmpty()) {
            // Use the fixed repository method with search
            return employeeRepository.getAllEmployeePins(search);
        } else {
            // Use simple method for all employees
            return employeeRepository.getAllEmployeePinsSimple();
        }
    }

    @Override
    public byte[] getProfilePictureByPin(String pin) {
        Employee employee = employeeRepository.findByPin(pin)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with PIN: " + pin));
        return employee.getProfilePicture();
    }

    @Override
    public String getImageTypeByPin(String pin) {
        Employee employee = employeeRepository.findByPin(pin)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with PIN: " + pin));
        return employee.getImageType();
    }

    // Additional useful methods - Add these to your EmployeeService interface

    @Override
    public EmployeeDTO updateEmployee(String pin, EmployeeUpdateDTO dto) {
        Employee employee = employeeRepository.findByPin(pin)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with PIN: " + pin));

        // Update fields if provided
        if (dto.getUsername() != null) employee.setUsername(dto.getUsername());
        if (dto.getEmail() != null) employee.setEmail(dto.getEmail());
        if (dto.getDesignation() != null) employee.setDesignation(dto.getDesignation());
        if (dto.getPhone() != null) employee.setPhone(dto.getPhone());
        if (dto.getBand() != null) employee.setBand(dto.getBand());
        if (dto.getAddress() != null) employee.setAddress(dto.getAddress());
        if (dto.getRole() != null) employee.setRole(dto.getRole());

        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDTO(savedEmployee);
    }

    @Override
    public void deactivateEmployee(String pin) {
        Employee employee = employeeRepository.findByPin(pin)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with PIN: " + pin));

        employee.setIsActive(false);
        employeeRepository.save(employee);
    }

    @Override
    public void activateEmployee(String pin) {
        Employee employee = employeeRepository.findByPin(pin)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with PIN: " + pin));

        employee.setIsActive(true);
        employeeRepository.save(employee);
    }

    @Override
    public BigDecimal getEmployeeBalance(String pin) {
        Employee employee = employeeRepository.findByPin(pin)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with PIN: " + pin));
        return employee.getBalance();
    }

    @Override
    public void updateEmployeeBalance(String pin, BigDecimal amount) {
        Employee employee = employeeRepository.findByPin(pin)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with PIN: " + pin));

        employee.setBalance(employee.getBalance().add(amount));
        employeeRepository.save(employee);
    }

    // Helper methods

    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = modelMapper.map(employee, EmployeeDTO.class);
        // Set additional fields if needed
        return dto;
    }

    private void validateImageFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        // Check file size (e.g., max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be less than 5MB");
        }
    }

    @Override
    public String getBalanceStatus(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            return "NEGATIVE";
        } else if (balance.compareTo(BigDecimal.ZERO) == 0) {
            return "ZERO";
        } else if (balance.compareTo(new BigDecimal("100")) < 0) {
            return "LOW";
        } else if (balance.compareTo(new BigDecimal("1000")) > 0) {
            return "HIGH";
        } else {
            return "NORMAL";
        }
    }

    @Override
    public List<Employee> saveEmployees(List<Employee> employees) {
        List<Employee> savedEmployees = new ArrayList<>();
        List<String> skippedEmployees = new ArrayList<>();

        if (employees == null || employees.isEmpty()) {
            return savedEmployees;
        }

        for (Employee employee : employees) {
            try {
                // Check if employee already exists
                if (employeeRepository.existsByUsername(employee.getUsername()) ||
                        employeeRepository.existsByEmail(employee.getEmail()) ||
                        employeeRepository.existsByPin(employee.getPin())) {
                    skippedEmployees.add("Duplicate: " + employee.getUsername());
                    continue;
                }

                // Set default values if needed
                prepareEmployeeForSave(employee);

                Employee saved = employeeRepository.save(employee);
                savedEmployees.add(saved);

            } catch (Exception e) {
                skippedEmployees.add("Error: " + employee.getUsername());
            }
        }

        return savedEmployees;
    }

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

        if (employee.getEmployeeId() == null || employee.getEmployeeId().isEmpty()) {
            employee.setEmployeeId(generateEmployeeId());
        }

        if (employee.getDesignation() == null || employee.getDesignation().isEmpty()) {
            employee.setDesignation("Employee");
        }

        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        if (employee.getCreatedAt() == null) {
            employee.setCreatedAt(now);
        }
        employee.setUpdatedAt(now);
    }

    private String generateEmployeeId() {
        long count = employeeRepository.count();
        return String.format("EMP%04d", count + 1);
    }
}
