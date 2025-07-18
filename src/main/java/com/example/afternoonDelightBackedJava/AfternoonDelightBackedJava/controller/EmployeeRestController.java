package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.controller;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.exception.DuplicateResourceException;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.EmployeeService;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeRestController {

    private final TransactionService transactionService;
    private final EmployeeService employeeService;

    @GetMapping("/employee-infos")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/employee-infos/by-username/{username}")
    public ResponseEntity<EmployeeDTO> getEmployeeByUsername(@PathVariable String username) {
        return ResponseEntity.ok(employeeService.getByUsername(username));
    }

    @GetMapping("/employee-infos/by-pin/{pin}")
    public ResponseEntity<EmployeeDTO> getEmployeeByPin(@PathVariable String pin) {
        return ResponseEntity.ok(employeeService.getByPin(pin));
    }

    @PostMapping("/employee-infos")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeCreationDTO dto) {
        try {
            EmployeeDTO createdEmployee = employeeService.createEmployee(dto);
            return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
        } catch (DuplicateResourceException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("DUPLICATE_RESOURCE", ex.getMessage()));
        }
    }

    @PostMapping("/employee-infos/by-username/{username}/upload-picture")
    public ResponseEntity<?> uploadPictureByUsername(
            @PathVariable String username,
            @RequestParam("file") MultipartFile file) {
        try {
            employeeService.uploadProfilePicture(username, file);
            return ResponseEntity.ok("Profile picture uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("UPLOAD_ERROR", "Failed to upload profile picture"));
        }
    }

    @PostMapping("/employee-infos/by-pin/{pin}/upload-picture")
    public ResponseEntity<?> uploadPictureByPin(
            @PathVariable String pin,
            @RequestParam("file") MultipartFile file) {
        try {
            employeeService.uploadProfilePictureByPin(pin, file);
            return ResponseEntity.ok("Profile picture uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("UPLOAD_ERROR", "Failed to upload profile picture"));
        }
    }

    @GetMapping("/employee-infos/by-username/{username}/profile-picture")
    public ResponseEntity<byte[]> getProfilePictureByUsername(@PathVariable String username) {
        byte[] image = employeeService.getProfilePicture(username);
        String imageType = employeeService.getImageType(username);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageType))
                .body(image);
    }

    @GetMapping("/employee-infos/by-pin/{pin}/profile-picture")
    public ResponseEntity<byte[]> getProfilePictureByPin(@PathVariable String pin) {
        byte[] image = employeeService.getProfilePictureByPin(pin);
        String imageType = employeeService.getImageTypeByPin(pin);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageType))
                .body(image);
    }

    @GetMapping("/employee-infos/by-pin/{pin}/transactions")
    public ResponseEntity<?> getTransactionHistory(
            @PathVariable String pin,
            @RequestParam(defaultValue = "1") int months) {

        if (months < 1 || months > 24) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("INVALID_PARAM", "Months must be between 1 and 24"));
        }

        return ResponseEntity.ok(transactionService.getTransactionHistory(pin, months));
    }

    @GetMapping("/employee-infos/pins")
    public ResponseEntity<List<EmployeePinDTO>> getEmployeePins(
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(employeeService.getAllEmployeePins(search));
    }

    public record ErrorResponse(String code, String message) {}
}