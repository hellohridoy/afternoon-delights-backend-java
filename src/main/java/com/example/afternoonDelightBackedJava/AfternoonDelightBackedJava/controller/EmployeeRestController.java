package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.controller;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.exception.DuplicateResourceException;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.EmployeeService;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmployeeRestController {

    private final TransactionService transactionService;
    
    private final EmployeeService employeeService;

    @GetMapping("/api/v1/employees/employee-infos")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        log.info("Received request to get all employees");

        try {
            List<EmployeeDTO> employees = employeeService.getAllEmployees();
            log.info("Successfully retrieved {} employees", employees.size());
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error retrieving all employees", e);
            throw e;
        }
    }

    @GetMapping("/api/v1/employees/employee-infos/by-username/{username}")
    public ResponseEntity<EmployeeDTO> getEmployeeByUsername(@PathVariable String username) {
        log.info("Received request to get employee by username: {}", username);

        try {
            EmployeeDTO employee = employeeService.getByUsername(username);
            log.info("Successfully retrieved employee by username: {}", username);
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            log.error("Error retrieving employee by username: {}", username, e);
            throw e;
        }
    }

    @GetMapping("/api/v1/employees/employee-infos/by-pin/{pin}")
    public ResponseEntity<EmployeeDTO> getEmployeeByPin(@PathVariable String pin) {
        log.info("Received request to get employee by PIN: {}", pin);

        try {
            EmployeeDTO employee = employeeService.getByPin(pin);
            log.info("Successfully retrieved employee by PIN: {}", pin);
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            log.error("Error retrieving employee by PIN: {}", pin, e);
            throw e;
        }
    }

    @PostMapping("/api/v1/employees/employee-infos")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeCreationDTO dto) {
        log.info("Received request to create employee with PIN: {} and username: {}",
                dto.getPin(), dto.getUsername());

        try {
            EmployeeDTO createdEmployee = employeeService.createEmployee(dto);
            log.info("Successfully created employee with PIN: {} and username: {}",
                    dto.getPin(), dto.getUsername());
            return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
        } catch (DuplicateResourceException ex) {
            log.warn("Employee creation failed due to duplicate resource - PIN: {}, username: {}, error: {}",
                    dto.getPin(), dto.getUsername(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("DUPLICATE_RESOURCE", ex.getMessage()));
        } catch (Exception e) {
            log.error("Error creating employee with PIN: {} and username: {}",
                    dto.getPin(), dto.getUsername(), e);
            throw e;
        }
    }

    @PostMapping("/api/v1/employees/employee-infos/by-username/{username}/upload-picture")
    public ResponseEntity<?> uploadPictureByUsername(
            @PathVariable String username,
            @RequestParam("file") MultipartFile file) {

        log.info("Received request to upload profile picture for username: {}, file: {}, size: {} bytes",
                username, file.getOriginalFilename(), file.getSize());

        try {
            employeeService.uploadProfilePicture(username, file);
            log.info("Successfully uploaded profile picture for username: {}", username);
            return ResponseEntity.ok("Profile picture uploaded successfully");
        } catch (IOException e) {
            log.error("IO error uploading profile picture for username: {}, file: {}",
                    username, file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("UPLOAD_ERROR", "Failed to upload profile picture"));
        } catch (Exception e) {
            log.error("Error uploading profile picture for username: {}", username, e);
            throw e;
        }
    }

    @PostMapping("/api/v1/employees/employee-infos/by-pin/{pin}/upload-picture")
    public ResponseEntity<?> uploadPictureByPin(
            @PathVariable String pin,
            @RequestParam("file") MultipartFile file) {

        log.info("Received request to upload profile picture for PIN: {}, file: {}, size: {} bytes",
                pin, file.getOriginalFilename(), file.getSize());

        try {
            employeeService.uploadProfilePictureByPin(pin, file);
            log.info("Successfully uploaded profile picture for PIN: {}", pin);
            return ResponseEntity.ok("Profile picture uploaded successfully");
        } catch (IOException e) {
            log.error("IO error uploading profile picture for PIN: {}, file: {}",
                    pin, file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("UPLOAD_ERROR", "Failed to upload profile picture"));
        } catch (Exception e) {
            log.error("Error uploading profile picture for PIN: {}", pin, e);
            throw e;
        }
    }

    @GetMapping("/api/v1/employees/employee-infos/by-username/{username}/profile-picture")
    public ResponseEntity<byte[]> getProfilePictureByUsername(@PathVariable String username) {
        log.info("Received request to get profile picture for username: {}", username);

        try {
            byte[] image = employeeService.getProfilePicture(username);
            String imageType = employeeService.getImageType(username);

            if (image != null && image.length > 0) {
                log.info("Successfully retrieved profile picture for username: {}, size: {} bytes, type: {}",
                        username, image.length, imageType);
            } else {
                log.info("No profile picture found for username: {}", username);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(imageType))
                    .body(image);
        } catch (Exception e) {
            log.error("Error retrieving profile picture for username: {}", username, e);
            throw e;
        }
    }

    @GetMapping("/api/v1/employees/employee-infos/by-pin/{pin}/profile-picture")
    public ResponseEntity<byte[]> getProfilePictureByPin(@PathVariable String pin) {
        log.info("Received request to get profile picture for PIN: {}", pin);

        try {
            byte[] image = employeeService.getProfilePictureByPin(pin);
            String imageType = employeeService.getImageTypeByPin(pin);

            if (image != null && image.length > 0) {
                log.info("Successfully retrieved profile picture for PIN: {}, size: {} bytes, type: {}",
                        pin, image.length, imageType);
            } else {
                log.info("No profile picture found for PIN: {}", pin);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(imageType))
                    .body(image);
        } catch (Exception e) {
            log.error("Error retrieving profile picture for PIN: {}", pin, e);
            throw e;
        }
    }

    @GetMapping("/api/v1/employees/employee-infos/by-pin/{pin}/transactions")
    public ResponseEntity<?> getTransactionHistory(
            @PathVariable String pin,
            @RequestParam(defaultValue = "1") int months) {

        log.info("Received request to get transaction history for PIN: {} for {} months", pin, months);

        if (months < 1 || months > 24) {
            log.warn("Invalid months parameter for transaction history request - PIN: {}, months: {}", pin, months);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("INVALID_PARAM", "Months must be between 1 and 24"));
        }

        try {
            Object transactionHistory = transactionService.getTransactionHistory(pin, months);
            log.info("Successfully retrieved transaction history for PIN: {} for {} months", pin, months);
            return ResponseEntity.ok(transactionHistory);
        } catch (Exception e) {
            log.error("Error retrieving transaction history for PIN: {} for {} months", pin, months, e);
            throw e;
        }
    }

    @GetMapping("/api/v1/employees/employee-infos/pins")
    public ResponseEntity<List<EmployeePinDTO>> getEmployeePins(
            @RequestParam(required = false) String search) {

        if (search != null && !search.trim().isEmpty()) {
            log.info("Received request to get employee PINs with search: '{}'", search);
        } else {
            log.info("Received request to get all employee PINs");
        }

        try {
            List<EmployeePinDTO> pins = employeeService.getAllEmployeePins(search);
            log.info("Successfully retrieved {} employee PINs{}",
                    pins.size(),
                    search != null && !search.trim().isEmpty() ? " for search: '" + search + "'" : "");
            return ResponseEntity.ok(pins);
        } catch (Exception e) {
            log.error("Error retrieving employee PINs with search: '{}'", search, e);
            throw e;
        }
    }

    public record ErrorResponse(String code, String message) {}
}