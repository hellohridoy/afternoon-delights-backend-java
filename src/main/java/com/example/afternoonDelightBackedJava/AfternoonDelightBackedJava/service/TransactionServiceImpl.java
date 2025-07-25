package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.TransactionDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.TransactionResponseDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Transaction;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<TransactionResponseDTO> getTransactionsByMeal(Long mealId) {
        log.info("Fetching transactions for meal ID: {}", mealId);

        try {
            if (mealId == null) {
                log.warn("Meal ID is null");
                throw new IllegalArgumentException("Meal ID cannot be null");
            }

            List<Transaction> transactions = transactionRepository.findByMealId(mealId);
            List<TransactionResponseDTO> result = transactions.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());

            log.info("Successfully retrieved {} transactions for meal ID: {}", result.size(), mealId);
            return result;

        } catch (DataAccessException e) {
            log.error("Database error while fetching transactions for meal ID {}: {}", mealId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve transactions due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching transactions for meal ID {}: {}", mealId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve transactions", e);
        }
    }

    @Override
    public TransactionResponseDTO createTransaction(TransactionDTO transactionDTO) {
        log.info("Creating new transaction for employee ID: {}", transactionDTO.getEmployeeId());

        try {
            if (transactionDTO == null) {
                log.warn("Transaction DTO is null");
                throw new IllegalArgumentException("Transaction data cannot be null");
            }

            log.debug("Mapping transaction DTO to entity");
            Transaction transaction = modelMapper.map(transactionDTO, Transaction.class);
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setUpdatedAt(LocalDateTime.now());

            log.debug("Saving transaction to database");
            Transaction savedTransaction = transactionRepository.save(transaction);

            TransactionResponseDTO result = mapToResponseDTO(savedTransaction);
            log.info("Successfully created transaction with ID: {} for employee ID: {}",
                    savedTransaction.getId(), transactionDTO.getEmployeeId());

            return result;

        } catch (DataAccessException e) {
            log.error("Database error while creating transaction for employee ID {}: {}",
                    transactionDTO != null ? transactionDTO.getEmployeeId() : "null", e.getMessage(), e);
            throw new RuntimeException("Failed to create transaction due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while creating transaction for employee ID {}: {}",
                    transactionDTO != null ? transactionDTO.getEmployeeId() : "null", e.getMessage(), e);
            throw new RuntimeException("Failed to create transaction", e);
        }
    }

    @Override
    public TransactionResponseDTO getTransactionById(Long id) {
        log.info("Fetching transaction by ID: {}", id);

        try {
            if (id == null) {
                log.warn("Transaction ID is null");
                throw new IllegalArgumentException("Transaction ID cannot be null");
            }

            Transaction transaction = transactionRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Transaction not found with ID: {}", id);
                        return new RuntimeException("Transaction not found with id: " + id);
                    });

            TransactionResponseDTO result = mapToResponseDTO(transaction);
            log.info("Successfully retrieved transaction with ID: {}", id);

            return result;

        } catch (DataAccessException e) {
            log.error("Database error while fetching transaction with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve transaction due to database error", e);
        } catch (Exception e) {
            log.error("Error while fetching transaction with ID {}: {}", id, e.getMessage(), e);
            throw e; // Re-throw as it might be a legitimate "not found" exception
        }
    }

    @Override
    public List<TransactionResponseDTO> getTransactionsByEmployee(Long employeeId) {
        log.info("Fetching transactions for employee ID: {}", employeeId);

        try {
            if (employeeId == null) {
                log.warn("Employee ID is null");
                throw new IllegalArgumentException("Employee ID cannot be null");
            }

            List<Transaction> transactions = transactionRepository.findByEmployeeId(employeeId);
            List<TransactionResponseDTO> result = transactions.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());

            log.info("Successfully retrieved {} transactions for employee ID: {}", result.size(), employeeId);
            return result;

        } catch (DataAccessException e) {
            log.error("Database error while fetching transactions for employee ID {}: {}", employeeId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve transactions due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching transactions for employee ID {}: {}", employeeId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve transactions", e);
        }
    }

    @Override
    public List<TransactionResponseDTO> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching transactions between {} and {}", startDate, endDate);

        try {
            if (startDate == null || endDate == null) {
                log.warn("Start date or end date is null: startDate={}, endDate={}", startDate, endDate);
                throw new IllegalArgumentException("Start date and end date cannot be null");
            }

            if (startDate.isAfter(endDate)) {
                log.warn("Start date {} is after end date {}", startDate, endDate);
                throw new IllegalArgumentException("Start date cannot be after end date");
            }

            List<Transaction> transactions = transactionRepository.findByTransactionDateBetween(startDate, endDate);
            List<TransactionResponseDTO> result = transactions.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());

            log.info("Successfully retrieved {} transactions between {} and {}", result.size(), startDate, endDate);
            return result;

        } catch (DataAccessException e) {
            log.error("Database error while fetching transactions between {} and {}: {}",
                    startDate, endDate, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve transactions due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching transactions between {} and {}: {}",
                    startDate, endDate, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve transactions", e);
        }
    }

    @Override
    public void deleteTransaction(Long id) {
        log.info("Deleting transaction with ID: {}", id);

        try {
            if (id == null) {
                log.warn("Transaction ID is null");
                throw new IllegalArgumentException("Transaction ID cannot be null");
            }

            Transaction transaction = transactionRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Transaction not found for deletion with ID: {}", id);
                        return new RuntimeException("Transaction not found with id: " + id);
                    });

            transactionRepository.delete(transaction);
            log.info("Successfully deleted transaction with ID: {}", id);

        } catch (DataAccessException e) {
            log.error("Database error while deleting transaction with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete transaction due to database error", e);
        } catch (Exception e) {
            log.error("Error while deleting transaction with ID {}: {}", id, e.getMessage(), e);
            throw e; // Re-throw as it might be a legitimate "not found" exception
        }
    }

    @Override
    public TransactionResponseDTO updateTransaction(Long id, TransactionDTO transactionDTO) {
        log.info("Updating transaction with ID: {}", id);

        try {
            if (id == null) {
                log.warn("Transaction ID is null");
                throw new IllegalArgumentException("Transaction ID cannot be null");
            }

            if (transactionDTO == null) {
                log.warn("Transaction DTO is null");
                throw new IllegalArgumentException("Transaction data cannot be null");
            }

            Transaction transaction = transactionRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Transaction not found for update with ID: {}", id);
                        return new RuntimeException("Transaction not found with id: " + id);
                    });

            log.debug("Mapping updated data to existing transaction entity");
            modelMapper.map(transactionDTO, transaction);
            transaction.setUpdatedAt(LocalDateTime.now());

            Transaction updatedTransaction = transactionRepository.save(transaction);
            TransactionResponseDTO result = mapToResponseDTO(updatedTransaction);

            log.info("Successfully updated transaction with ID: {}", id);
            return result;

        } catch (DataAccessException e) {
            log.error("Database error while updating transaction with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update transaction due to database error", e);
        } catch (Exception e) {
            log.error("Error while updating transaction with ID {}: {}", id, e.getMessage(), e);
            throw e; // Re-throw as it might be a legitimate "not found" exception
        }
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        try {
            if (transaction == null) {
                log.warn("Attempted to map null transaction to DTO");
                return null;
            }

            return modelMapper.map(transaction, TransactionResponseDTO.class);

        } catch (Exception e) {
            log.error("Error mapping transaction {} to response DTO: {}",
                    transaction.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to map transaction data", e);
        }
    }

    public List<TransactionResponseDTO> getTransactionHistory(String pin, int months) {
        log.info("Fetching transaction history for employee pin: {} for {} months", pin, months);

        try {
            if (pin == null || pin.trim().isEmpty()) {
                log.warn("Employee pin is null or empty");
                throw new IllegalArgumentException("Employee pin cannot be null or empty");
            }

            if (months < 0) {
                log.warn("Invalid months parameter: {}", months);
                throw new IllegalArgumentException("Months cannot be negative");
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(months);

            log.debug("Fetching transactions for pin {} between {} and {}", pin, startDate, endDate);

            List<Transaction> transactions = transactionRepository
                    .findByEmployeePinAndTransactionDateBetween(pin, startDate, endDate);

            List<TransactionResponseDTO> result = transactions.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            log.info("Successfully retrieved {} transactions for employee pin: {} for {} months",
                    result.size(), pin, months);

            return result;

        } catch (DataAccessException e) {
            log.error("Database error while fetching transaction history for pin {} for {} months: {}",
                    pin, months, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve transaction history due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching transaction history for pin {} for {} months: {}",
                    pin, months, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve transaction history", e);
        }
    }

    @Override
    public TransactionResponseDTO convertToDTO(Transaction transaction) {
        try {
            if (transaction == null) {
                log.warn("Attempted to convert null transaction to DTO");
                return null;
            }

            TransactionResponseDTO dto = new TransactionResponseDTO();
            dto.setId(transaction.getId());
            dto.setAmount(transaction.getAmount());
            dto.setTransactionDate(transaction.getTransactionDate());
            dto.setDescription(transaction.getDescription());

            if (transaction.getMeal() != null) {
                dto.setMealId(transaction.getMeal().getId());
                if (transaction.getMeal().getFoodItem() != null) {
                    dto.setMealName(transaction.getMeal().getFoodItem().getName());
                } else {
                    log.warn("FoodItem is null for meal ID: {} in transaction ID: {}",
                            transaction.getMeal().getId(), transaction.getId());
                }
            } else {
                log.debug("No meal associated with transaction ID: {}", transaction.getId());
            }

            return dto;

        } catch (Exception e) {
            log.error("Error converting transaction {} to DTO: {}",
                    transaction != null ? transaction.getId() : "null", e.getMessage(), e);
            throw new RuntimeException("Failed to convert transaction data", e);
        }
    }
}