package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.TransactionDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.TransactionResponseDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    List<TransactionResponseDTO> getTransactionsByMeal(Long mealId);

    TransactionResponseDTO createTransaction(TransactionDTO transactionDTO);

    TransactionResponseDTO getTransactionById(Long id);

    List<TransactionResponseDTO> getTransactionsByEmployee(Long employeeId);

    List<TransactionResponseDTO> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate);

    void deleteTransaction(Long id);

    List<TransactionResponseDTO> getTransactionHistory(String pin, int months);

    TransactionResponseDTO updateTransaction(Long id, TransactionDTO transactionDTO);

    TransactionResponseDTO convertToDTO(Transaction transaction);
}
