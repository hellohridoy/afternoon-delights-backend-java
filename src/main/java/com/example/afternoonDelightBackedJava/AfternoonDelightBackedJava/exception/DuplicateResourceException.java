package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class DuplicateResourceException extends RuntimeException {
    private final HttpStatus status;
    private final String error;
    private final String details;
    public DuplicateResourceException(String message, HttpStatus status, String error, String details) {
        super(message);
        this.status = status;
        this.error = error;
        this.details = details;
    }
}