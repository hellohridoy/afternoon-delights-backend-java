package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.emums.Role;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public Optional<Employee> findByEmployeePin(String pin) {
        log.debug("Finding employee by PIN: {}", pin);
        Optional<Employee> employee = employeeRepository.findByPin(pin);
        if (employee.isPresent()) {
            log.debug("Employee found with PIN: {}", pin);
        } else {
            log.debug("No employee found with PIN: {}", pin);
        }
        return employee;
    }

    @Override
    public EmployeeDTO getByUsername(String username) {
        log.info("Getting employee by username: {}", username);
        try {
            Employee employee = employeeRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("Employee not found with username: {}", username);
                        return new EntityNotFoundException("Employee not found with username: " + username);
                    });

            EmployeeDTO dto = convertToDTO(employee);
            log.info("Successfully retrieved employee by username: {}", username);
            return dto;
        } catch (Exception e) {
            log.error("Error retrieving employee by username: {}", username, e);
            throw e;
        }
    }

    @Override
    public EmployeeDTO getByPin(String pin) {
        log.info("Getting employee by PIN: {}", pin);
        try {
            Employee employee = employeeRepository.findByPin(pin)
                    .orElseThrow(() -> {
                        log.error("Employee not found with PIN: {}", pin);
                        return new EntityNotFoundException("Employee not found with PIN: " + pin);
                    });

            EmployeeDTO dto = convertToDTO(employee);
            log.info("Successfully retrieved employee by PIN: {}", pin);
            return dto;
        } catch (Exception e) {
            log.error("Error retrieving employee by PIN: {}", pin, e);
            throw e;
        }
    }

    @Override
    public void uploadProfilePicture(String username, MultipartFile file) throws IOException {
        log.info("Uploading profile picture for username: {}, file size: {} bytes",
                username, file.getSize());

        try {
            Employee employee = employeeRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("Employee not found with username: {}", username);
                        return new EntityNotFoundException("Employee not found with username: " + username);
                    });

            // Validate file type
            validateImageFile(file);
            log.debug("Image file validation passed for username: {}", username);

            employee.setProfilePicture(file.getBytes());
            employee.setImageType(file.getContentType());
            employeeRepository.save(employee);

            log.info("Successfully uploaded profile picture for username: {}", username);
        } catch (Exception e) {
            log.error("Error uploading profile picture for username: {}", username, e);
            throw e;
        }
    }

    @Override
    public void uploadProfilePictureByPin(String pin, MultipartFile file) throws IOException {
        log.info("Uploading profile picture for PIN: {}, file size: {} bytes",
                pin, file.getSize());

        try {
            Employee employee = employeeRepository.findByPin(pin)
                    .orElseThrow(() -> {
                        log.error("Employee not found with PIN: {}", pin);
                        return new EntityNotFoundException("Employee not found with PIN: " + pin);
                    });

            // Validate file type
            validateImageFile(file);
            log.debug("Image file validation passed for PIN: {}", pin);

            employee.setProfilePicture(file.getBytes());
            employee.setImageType(file.getContentType());
            employeeRepository.save(employee);

            log.info("Successfully uploaded profile picture for PIN: {}", pin);
        } catch (Exception e) {
            log.error("Error uploading profile picture for PIN: {}", pin, e);
            throw e;
        }
    }

    @Override
    public byte[] getProfilePicture(String username) {
        log.debug("Getting profile picture for username: {}", username);
        try {
            Employee employee = employeeRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("Employee not found with username: {}", username);
                        return new EntityNotFoundException("Employee not found with username: " + username);
                    });

            byte[] picture = employee.getProfilePicture();
            if (picture != null) {
                log.debug("Profile picture retrieved for username: {}, size: {} bytes",
                        username, picture.length);
            } else {
                log.debug("No profile picture found for username: {}", username);
            }
            return picture;
        } catch (Exception e) {
            log.error("Error getting profile picture for username: {}", username, e);
            throw e;
        }
    }

    @Override
    public String getImageType(String username) {
        log.debug("Getting image type for username: {}", username);
        try {
            Employee employee = employeeRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("Employee not found with username: {}", username);
                        return new EntityNotFoundException("Employee not found with username: " + username);
                    });

            String imageType = employee.getImageType();
            log.debug("Image type for username {}: {}", username, imageType);
            return imageType;
        } catch (Exception e) {
            log.error("Error getting image type for username: {}", username, e);
            throw e;
        }
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        log.info("Getting all employees");
        try {
            List<EmployeeDTO> employees = employeeRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            log.info("Successfully retrieved {} employees", employees.size());
            return employees;
        } catch (Exception e) {
            log.error("Error getting all employees", e);
            throw e;
        }
    }

    @Override
    public EmployeeDTO createEmployee(EmployeeCreationDTO dto) {
        log.info("Creating new employee with PIN: {} and username: {}", dto.getPin(), dto.getUsername());

        try {
            // Check if employee already exists
            if (employeeRepository.findByPin(dto.getPin()).isPresent()) {
                log.warn("Employee creation failed - PIN already exists: {}", dto.getPin());
                throw new RuntimeException("Employee with PIN " + dto.getPin() + " already exists");
            }
            if (employeeRepository.findByEmail(dto.getEmail()).isPresent()) {
                log.warn("Employee creation failed - Email already exists: {}", dto.getEmail());
                throw new RuntimeException("Employee with email " + dto.getEmail() + " already exists");
            }
            if (employeeRepository.findByUsername(dto.getUsername()).isPresent()) {
                log.warn("Employee creation failed - Username already exists: {}", dto.getUsername());
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
            EmployeeDTO result = convertToDTO(savedEmployee);

            log.info("Successfully created employee with PIN: {} and ID: {}",
                    dto.getPin(), savedEmployee.getId());
            return result;

        } catch (Exception e) {
            log.error("Error creating employee with PIN: {}", dto.getPin(), e);
            throw e;
        }
    }

    @Override
    public List<EmployeePinDTO> getAllEmployeePins(String search) {
        log.info("Getting all employee PINs with search: {}", search);

        try {
            List<EmployeePinDTO> pins;
            if (search != null && !search.trim().isEmpty()) {
                // Use the fixed repository method with search
                pins = employeeRepository.getAllEmployeePins(search);
                log.debug("Found {} employee PINs matching search: {}", pins.size(), search);
            } else {
                // Use simple method for all employees
                pins = employeeRepository.getAllEmployeePinsSimple();
                log.debug("Found {} total employee PINs", pins.size());
            }

            log.info("Successfully retrieved {} employee PINs", pins.size());
            return pins;

        } catch (Exception e) {
            log.error("Error getting employee PINs with search: {}", search, e);
            throw e;
        }
    }

    @Override
    public byte[] getProfilePictureByPin(String pin) {
        log.debug("Getting profile picture for PIN: {}", pin);
        try {
            Employee employee = employeeRepository.findByPin(pin)
                    .orElseThrow(() -> {
                        log.error("Employee not found with PIN: {}", pin);
                        return new EntityNotFoundException("Employee not found with PIN: " + pin);
                    });

            byte[] picture = employee.getProfilePicture();
            if (picture != null) {
                log.debug("Profile picture retrieved for PIN: {}, size: {} bytes",
                        pin, picture.length);
            } else {
                log.debug("No profile picture found for PIN: {}", pin);
            }
            return picture;
        } catch (Exception e) {
            log.error("Error getting profile picture for PIN: {}", pin, e);
            throw e;
        }
    }

    @Override
    public String getImageTypeByPin(String pin) {
        log.debug("Getting image type for PIN: {}", pin);
        try {
            Employee employee = employeeRepository.findByPin(pin)
                    .orElseThrow(() -> {
                        log.error("Employee not found with PIN: {}", pin);
                        return new EntityNotFoundException("Employee not found with PIN: " + pin);
                    });

            String imageType = employee.getImageType();
            log.debug("Image type for PIN {}: {}", pin, imageType);
            return imageType;
        } catch (Exception e) {
            log.error("Error getting image type for PIN: {}", pin, e);
            throw e;
        }
    }

    @Override
    public EmployeeDTO updateEmployee(String pin, EmployeeUpdateDTO dto) {
        log.info("Updating employee with PIN: {}", pin);

        try {
            Employee employee = employeeRepository.findByPin(pin)
                    .orElseThrow(() -> {
                        log.error("Employee not found with PIN: {}", pin);
                        return new EntityNotFoundException("Employee not found with PIN: " + pin);
                    });

            // Track what fields are being updated
            List<String> updatedFields = new ArrayList<>();

            // Update fields if provided
            if (dto.getUsername() != null) {
                employee.setUsername(dto.getUsername());
                updatedFields.add("username");
            }
            if (dto.getEmail() != null) {
                employee.setEmail(dto.getEmail());
                updatedFields.add("email");
            }
            if (dto.getDesignation() != null) {
                employee.setDesignation(dto.getDesignation());
                updatedFields.add("designation");
            }
            if (dto.getPhone() != null) {
                employee.setPhone(dto.getPhone());
                updatedFields.add("phone");
            }
            if (dto.getBand() != null) {
                employee.setBand(dto.getBand());
                updatedFields.add("band");
            }
            if (dto.getAddress() != null) {
                employee.setAddress(dto.getAddress());
                updatedFields.add("address");
            }
            if (dto.getRole() != null) {
                employee.setRole(dto.getRole());
                updatedFields.add("role");
            }

            Employee savedEmployee = employeeRepository.save(employee);
            EmployeeDTO result = convertToDTO(savedEmployee);

            log.info("Successfully updated employee with PIN: {}, updated fields: {}",
                    pin, String.join(", ", updatedFields));
            return result;

        } catch (Exception e) {
            log.error("Error updating employee with PIN: {}", pin, e);
            throw e;
        }
    }

    @Override
    public void deactivateEmployee(String pin) {
        log.info("Deactivating employee with PIN: {}", pin);

        try {
            Employee employee = employeeRepository.findByPin(pin)
                    .orElseThrow(() -> {
                        log.error("Employee not found with PIN: {}", pin);
                        return new EntityNotFoundException("Employee not found with PIN: " + pin);
                    });

            employee.setIsActive(false);
            employeeRepository.save(employee);

            log.info("Successfully deactivated employee with PIN: {}", pin);
        } catch (Exception e) {
            log.error("Error deactivating employee with PIN: {}", pin, e);
            throw e;
        }
    }

    @Override
    public void activateEmployee(String pin) {
        log.info("Activating employee with PIN: {}", pin);

        try {
            Employee employee = employeeRepository.findByPin(pin)
                    .orElseThrow(() -> {
                        log.error("Employee not found with PIN: {}", pin);
                        return new EntityNotFoundException("Employee not found with PIN: " + pin);
                    });

            employee.setIsActive(true);
            employeeRepository.save(employee);

            log.info("Successfully activated employee with PIN: {}", pin);
        } catch (Exception e) {
            log.error("Error activating employee with PIN: {}", pin, e);
            throw e;
        }
    }

    @Override
    public BigDecimal getEmployeeBalance(String pin) {
        log.debug("Getting balance for employee with PIN: {}", pin);

        try {
            Employee employee = employeeRepository.findByPin(pin)
                    .orElseThrow(() -> {
                        log.error("Employee not found with PIN: {}", pin);
                        return new EntityNotFoundException("Employee not found with PIN: " + pin);
                    });

            BigDecimal balance = employee.getBalance();
            log.debug("Retrieved balance {} for employee with PIN: {}", balance, pin);
            return balance;
        } catch (Exception e) {
            log.error("Error getting balance for employee with PIN: {}", pin, e);
            throw e;
        }
    }

    @Override
    public void updateEmployeeBalance(String pin, BigDecimal amount) {
        log.info("Updating balance for employee with PIN: {} by amount: {}", pin, amount);

        try {
            Employee employee = employeeRepository.findByPin(pin)
                    .orElseThrow(() -> {
                        log.error("Employee not found with PIN: {}", pin);
                        return new EntityNotFoundException("Employee not found with PIN: " + pin);
                    });

            BigDecimal oldBalance = employee.getBalance();
            BigDecimal newBalance = oldBalance.add(amount);
            employee.setBalance(newBalance);
            employeeRepository.save(employee);

            log.info("Successfully updated balance for employee with PIN: {} from {} to {} (change: {})",
                    pin, oldBalance, newBalance, amount);
        } catch (Exception e) {
            log.error("Error updating balance for employee with PIN: {}", pin, e);
            throw e;
        }
    }

    @Override
    public String getBalanceStatus(BigDecimal balance) {
        log.trace("Determining balance status for amount: {}", balance);

        String status;
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            status = "NEGATIVE";
        } else if (balance.compareTo(BigDecimal.ZERO) == 0) {
            status = "ZERO";
        } else if (balance.compareTo(new BigDecimal("100")) < 0) {
            status = "LOW";
        } else if (balance.compareTo(new BigDecimal("1000")) > 0) {
            status = "HIGH";
        } else {
            status = "NORMAL";
        }

        log.trace("Balance status for {}: {}", balance, status);
        return status;
    }

    @Override
    public List<Employee> saveEmployees(List<Employee> employees) {
        log.info("Bulk saving {} employees", employees != null ? employees.size() : 0);

        List<Employee> savedEmployees = new ArrayList<>();
        List<String> skippedEmployees = new ArrayList<>();

        if (employees == null || employees.isEmpty()) {
            log.warn("No employees provided for bulk save operation");
            return savedEmployees;
        }

        try {
            for (Employee employee : employees) {
                try {
                    // Check if employee already exists
                    if (employeeRepository.existsByUsername(employee.getUsername()) ||
                            employeeRepository.existsByEmail(employee.getEmail()) ||
                            employeeRepository.existsByPin(employee.getPin())) {
                        skippedEmployees.add("Duplicate: " + employee.getUsername());
                        log.debug("Skipped duplicate employee: {}", employee.getUsername());
                        continue;
                    }

                    // Set default values if needed
                    prepareEmployeeForSave(employee);

                    Employee saved = employeeRepository.save(employee);
                    savedEmployees.add(saved);
                    log.debug("Successfully saved employee: {}", employee.getUsername());

                } catch (Exception e) {
                    skippedEmployees.add("Error: " + employee.getUsername());
                    log.error("Error saving employee: {}", employee.getUsername(), e);
                }
            }

            log.info("Bulk save completed. Saved: {}, Skipped: {}, Total processed: {}",
                    savedEmployees.size(), skippedEmployees.size(), employees.size());

            if (!skippedEmployees.isEmpty()) {
                log.warn("Skipped employees: {}", skippedEmployees);
            }

            return savedEmployees;

        } catch (Exception e) {
            log.error("Error during bulk employee save operation", e);
            throw e;
        }
    }

    // Helper methods
    private EmployeeDTO convertToDTO(Employee employee) {
        log.trace("Converting employee {} to DTO", employee.getPin());
        EmployeeDTO dto = modelMapper.map(employee, EmployeeDTO.class);
        // Set additional fields if needed
        return dto;
    }

    private void validateImageFile(MultipartFile file) throws IOException {
        log.debug("Validating image file: {}, size: {} bytes, content type: {}",
                file.getOriginalFilename(), file.getSize(), file.getContentType());

        if (file.isEmpty()) {
            log.error("Image file validation failed: File is empty");
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            log.error("Image file validation failed: Invalid content type: {}", contentType);
            throw new IllegalArgumentException("File must be an image");
        }

        // Check file size (e.g., max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            log.error("Image file validation failed: File size {} exceeds 5MB limit", file.getSize());
            throw new IllegalArgumentException("File size must be less than 5MB");
        }

        log.debug("Image file validation passed");
    }

    private void prepareEmployeeForSave(Employee employee) {
        log.trace("Preparing employee {} for save", employee.getUsername());

        // Set default values
        if (employee.getBalance() == null) {
            employee.setBalance(BigDecimal.ZERO);
            log.trace("Set default balance to ZERO for employee: {}", employee.getUsername());
        }

        if (employee.getRole() == null) {
            employee.setRole(Role.EMPLOYEE);
            log.trace("Set default role to EMPLOYEE for employee: {}", employee.getUsername());
        }

        if (employee.getIsActive() == null) {
            employee.setIsActive(true);
            log.trace("Set default active status to true for employee: {}", employee.getUsername());
        }

        if (employee.getEmployeeId() == null || employee.getEmployeeId().isEmpty()) {
            String generatedId = generateEmployeeId();
            employee.setEmployeeId(generatedId);
            log.trace("Generated employee ID {} for employee: {}", generatedId, employee.getUsername());
        }

        if (employee.getDesignation() == null || employee.getDesignation().isEmpty()) {
            employee.setDesignation("Employee");
            log.trace("Set default designation to 'Employee' for employee: {}", employee.getUsername());
        }

        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        if (employee.getCreatedAt() == null) {
            employee.setCreatedAt(now);
        }
        employee.setUpdatedAt(now);

        log.trace("Employee preparation completed for: {}", employee.getUsername());
    }

    private String generateEmployeeId() {
        log.debug("Generating new employee ID");
        long count = employeeRepository.count();
        String employeeId = String.format("EMP%04d", count + 1);
        log.debug("Generated employee ID: {}", employeeId);
        return employeeId;
    }
}