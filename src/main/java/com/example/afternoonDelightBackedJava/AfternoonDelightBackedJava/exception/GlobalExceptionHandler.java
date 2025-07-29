package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.exception;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.error.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
 
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException methodArgumentNotValidException, HttpHeaders httpHeaders, HttpStatusCode httpStatusCode, WebRequest webRequest
            ){
        Map<String, String> errors = new HashMap<>();
        methodArgumentNotValidException.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ErrorResponse errorResponse = new ErrorResponse(
                Timestamp.from(Instant.now()),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Invalid request parameters",
                ((ServletWebRequest)webRequest).getRequest().getRequestURI(),
                errors.toString()
                
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                Timestamp.from(Instant.now()),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI(),
                ex.getClass().getName()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(DuplicateResourceException.class)
    public final ResponseEntity<ErrorResponse> handleCustomExceptions(DuplicateResourceException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                Timestamp.from(Instant.now()),
                ex.getStatus().value(),
                ex.getError(),
                ex.getMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI(),
                ex.getDetails()
        );
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }
}
