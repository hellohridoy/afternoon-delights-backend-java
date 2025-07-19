package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.error.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class ErrorResponse {
    private Timestamp timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String details;

    // Add builder-style constructor for better readability
    public ErrorResponse(Timestamp timestamp, int status, String error,
                         String message, String path, String details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }
}