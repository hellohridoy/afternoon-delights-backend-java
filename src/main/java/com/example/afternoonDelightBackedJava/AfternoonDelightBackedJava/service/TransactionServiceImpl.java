package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.TransactionDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.TransactionResponseDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Transaction;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {
    
    private final TransactionRepository transactionRepository;
 
    private final ModelMapper modelMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository, ModelMapper modelMapper) {
        this.transactionRepository = transactionRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TransactionResponseDTO> getTransactionsByMeal(Long mealId) {
        List<Transaction> transactions = transactionRepository.findByMealId(mealId);
        return transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponseDTO createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = modelMapper.map(transactionDTO, Transaction.class);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToResponseDTO(savedTransaction);
    }

    @Override
    public TransactionResponseDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        return mapToResponseDTO(transaction);
    }

    @Override
    public List<TransactionResponseDTO> getTransactionsByEmployee(Long employeeId) {
        List<Transaction> transactions = transactionRepository.findByEmployeeId(employeeId);
        return transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponseDTO> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findByTransactionDateBetween(startDate, endDate);
        return transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        transactionRepository.delete(transaction);
    }

    @Override
    public TransactionResponseDTO updateTransaction(Long id, TransactionDTO transactionDTO) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        modelMapper.map(transactionDTO, transaction);
        transaction.setUpdatedAt(LocalDateTime.now());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return mapToResponseDTO(updatedTransaction);
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        return modelMapper.map(transaction, TransactionResponseDTO.class);
    }
}
