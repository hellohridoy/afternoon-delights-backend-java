package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;


import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result DTO for bulk employee import operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeBulkImportResult {

    private int totalProvided;
    private int successfullyImported;
    private int skipped;
    private List<Employee> importedEmployees;
    private List<String> errors;
    private List<String> warnings;

    // Computed properties
    public double getSuccessRate() {
        if (totalProvided == 0) return 0.0;
        return (double) successfullyImported / totalProvided * 100.0;
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }

    public boolean isCompleteSuccess() {
        return skipped == 0 && !hasErrors();
    }

    public String getSummary() {
        return String.format("Import completed: %d/%d successful (%.1f%%), %d skipped",
                successfullyImported, totalProvided, getSuccessRate(), skipped);
    }
}
