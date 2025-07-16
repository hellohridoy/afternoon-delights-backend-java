package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.controller;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeePersonalInfoDto;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EmployeeRestController {

    private final EmployeeService employeeService;

    @GetMapping("/api/employees/employee-infos")
    public ResponseEntity<List<EmployeePersonalInfoDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/api/employees/employee-infos/{username}")
    public ResponseEntity<EmployeePersonalInfoDto> getEmployeeByUsername(@PathVariable String username) {
        return ResponseEntity.ok(employeeService.getByUsername(username));
    }

    @GetMapping("/api/employees/employee-infos/by-pin/{pin}")
    public ResponseEntity<EmployeePersonalInfoDto> getEmployeeByPin(@PathVariable String pin) {
        return ResponseEntity.ok(employeeService.getByPin(pin));
    }

    @PostMapping("/api/employees/employee-infos")
    public ResponseEntity<EmployeePersonalInfoDto> createEmployee(@RequestBody EmployeePersonalInfoDto dto) {
        return new ResponseEntity<>(employeeService.createEmployee(dto), HttpStatus.CREATED);
    }

    @PostMapping("/api/employees/employee-infos/{username}/upload-picture")
    public ResponseEntity<String> uploadPicture(@PathVariable String username,
                                                @RequestParam("file") MultipartFile file) {
        try {
            employeeService.uploadProfilePicture(username, file);
            return ResponseEntity.ok("Profile picture uploaded");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

    @PostMapping("/api/employees/employee-infos/by-pin/{pin}/upload-picture")
    public ResponseEntity<String> uploadPictureByPin(@PathVariable String pin,
                                                @RequestParam("file") MultipartFile file) {
        try {
            employeeService.uploadProfilePictureByPin(pin, file);
            return ResponseEntity.ok("Profile picture uploaded");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

    @GetMapping("/api/employees/employee-infos/{username}/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String username) {
        byte[] image = employeeService.getProfilePicture(username);
        String imageType = employeeService.getImageType(username);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageType))
                .body(image);
    }
}
