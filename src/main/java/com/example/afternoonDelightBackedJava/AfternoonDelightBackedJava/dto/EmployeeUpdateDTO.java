package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.emums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating employee information
 * Contains only fields that can be safely updated after employee creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeUpdateDTO {

    @Size(min = 3, max = 50, message = "Username must be between 3-50 characters")
    private String username;

    @Email(message = "Email should be valid")
    private String email;

    private String designation;

    @Pattern(regexp = "\\+?[0-9\\s-]{10,15}", message = "Invalid phone number format")
    private String phone;

    private String band;

    private String address;

    private Role role;

    // Optional: Manager assignment (if you're using the manager relationship)
    private String managerPin;

    // Validation method to check if at least one field is provided
    public boolean hasUpdates() {
        return username != null ||
                email != null ||
                designation != null ||
                phone != null ||
                band != null ||
                address != null ||
                role != null ||
                managerPin != null;
    }

    // Helper method to check if critical fields are being updated
    public boolean hasCriticalUpdates() {
        return username != null || email != null || role != null;
    }
}