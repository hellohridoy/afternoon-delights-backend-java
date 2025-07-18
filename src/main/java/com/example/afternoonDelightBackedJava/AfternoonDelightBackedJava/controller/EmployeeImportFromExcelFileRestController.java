package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.controller;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.EmployeeUploadFromExcelFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequiredArgsConstructor
public class EmployeeImportFromExcelFileRestController {
    
    private final EmployeeUploadFromExcelFileService employeeImportService;

    @PostMapping("/api/v1/upload-employee/from-excel-file/import")
    public ResponseEntity<Map<String, Object>> importEmployees(
            @RequestParam("file") MultipartFile file) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validate file
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select a file to upload");
                return ResponseEntity.badRequest().body(response);
            }

            // Check file type
            String filename = file.getOriginalFilename();
            if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
                response.put("success", false);
                response.put("message", "Please upload an Excel file (.xlsx)");
                return ResponseEntity.badRequest().body(response);
            }

            // Import employees
            List<Employee> importedEmployees = employeeImportService.importEmployeesFromExcel(file);

            response.put("success", true);
            response.put("message", "Employees imported successfully");
            response.put("totalImported", importedEmployees.size());
            response.put("employees", importedEmployees);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error importing employees: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/api/v1/upload-employee/from-excel-file/import/template")
    public ResponseEntity<Map<String, Object>> getImportTemplate() {
        Map<String, Object> response = new HashMap<>();

        String[] headers = {
                "Employee ID", "Username", "Email", "Designation",
                "Phone", "Band", "Address", "PIN"
        };

        String[] sampleData = {
                "1001", "john.doe", "john.doe@company.com", "Software Engineer",
                "123-456-7890", "Band A", "123 Main St", "1234"
        };

        response.put("headers", headers);
        response.put("sampleData", sampleData);
        response.put("instructions", "Please follow this format when creating your Excel file. " +
                "Employee ID, Username, Email, and PIN are required fields. " +
                "PIN must be exactly 4 digits.");

        return ResponseEntity.ok(response);
    }
}
