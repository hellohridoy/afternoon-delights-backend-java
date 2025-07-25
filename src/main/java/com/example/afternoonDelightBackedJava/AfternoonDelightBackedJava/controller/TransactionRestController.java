package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.controller;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.TransactionDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.TransactionResponseDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionRestController {

    private final TransactionService transactionService;

    @PostMapping("/api/v1/transactions/transactions-infos")
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @RequestBody TransactionDTO transactionDTO) {
        log.info("Creating transaction for employee ID: {}, amount: {}",
                transactionDTO.getEmployeeId(), transactionDTO.getAmount());
        TransactionResponseDTO response = transactionService.createTransaction(transactionDTO);
        log.info("Transaction created with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/transactions/transactions-infos/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(
            @PathVariable Long id) {
        log.info("Getting transaction by ID: {}", id);
        TransactionResponseDTO response = transactionService.getTransactionById(id);
        log.info("Retrieved transaction ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/transactions/transactions-infos/employee/{employeeId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByEmployee(
            @PathVariable Long employeeId) {
        log.info("Getting transactions for employee ID: {}", employeeId);
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByEmployee(employeeId);
        log.info("Retrieved {} transactions for employee ID: {}", transactions.size(), employeeId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/api/v1/transactions/transactions-infos/date-range")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Getting transactions for date range: {} to {}", startDate, endDate);
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
        log.info("Retrieved {} transactions for date range", transactions.size());
        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/api/v1/transactions/transactions-infos/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionDTO transactionDTO) {
        log.info("Updating transaction ID: {}", id);
        TransactionResponseDTO response = transactionService.updateTransaction(id, transactionDTO);
        log.info("Updated transaction ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/v1/transactions/transactions-infos/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        log.info("Deleting transaction ID: {}", id);
        transactionService.deleteTransaction(id);
        log.info("Deleted transaction ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}