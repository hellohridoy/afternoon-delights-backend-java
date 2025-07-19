package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MealServiceImpl implements MealService {

    private final MealRepository mealRepository;
    private final FoodItemRepository foodItemRepository;
    private final TransactionRepository transactionRepository;
    private final EmployeeRepository employeeRepository;
    private final MealParticipationRepository participationRepository;
    private final TransactionService transactionService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Meal createMeal(MealCreationDTO mealDTO) {
        // Find or create food item
        FoodItem foodItem = foodItemRepository.findByName(mealDTO.getFoodItem())
                .orElseGet(() -> {
                    FoodItem newItem = new FoodItem();
                    newItem.setName(mealDTO.getFoodItem());
                    return foodItemRepository.save(newItem);
                });

        // Create meal
        Meal meal = new Meal();
        meal.setMealDate(mealDTO.getMealDate());
        meal.setFoodItem(foodItem);  // Set the FoodItem object
        meal.setTotalCost(BigDecimal.valueOf(mealDTO.getTotalCost()));
        meal.setCreatedAt(LocalDateTime.now());
        meal.setUpdatedAt(LocalDateTime.now());

        // Calculate participants and per-head cost
        List<String> participants = mealDTO.getParticipants();
        int participantCount = participants != null ? participants.size() : 0;
        meal.setTotalParticipants(participantCount);

        BigDecimal perHeadCost = participantCount > 0 ?
                meal.getTotalCost().divide(BigDecimal.valueOf(participantCount), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;
        meal.setPerHeadCost(perHeadCost);

        // Save meal first
        meal = mealRepository.save(meal);

        // Process participants if any
        if (participants != null && !participants.isEmpty()) {
            for (String pin : participants) {
                Employee employee = employeeRepository.findByPin(pin)
                        .orElseThrow(() -> new RuntimeException("Employee not found with PIN: " + pin));
                addParticipant(meal, employee);
            }
        }

        return meal;
    }
    @Override
    public MealResponseDTO getMealByDate(LocalDate date) {
        Meal meal = mealRepository.findByMealDate(date)
                .orElseThrow(() -> new RuntimeException("Meal not found for date: " + date));

        MealResponseDTO response = new MealResponseDTO();
        response.setMealDate(meal.getMealDate());
        response.setFoodItemName(meal.getFoodItem().getName());
        response.setTotalCost(meal.getTotalCost());

        List<String> participantPins = meal.getParticipations().stream()
                .map(mp -> mp.getEmployee().getPin())
                .toList();
        response.setParticipants(participantPins);

        return response;
    }

    @Override
    @Transactional
    public MealResponseDTO updateMeal(Long id, MealRequestDTO mealRequestDTO) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found with id: " + id));

        // Update basic info
        meal.setMealDate(mealRequestDTO.getMealDate());
        meal.setTotalCost(mealRequestDTO.getTotalCost());
        meal.setUpdatedAt(LocalDateTime.now());

        // Update food item if changed
        if (!meal.getFoodItem().getName().equals(mealRequestDTO.getFoodItemName())) {
            FoodItem foodItem = foodItemRepository.findByName(mealRequestDTO.getFoodItemName())
                    .orElseGet(() -> {
                        FoodItem newFoodItem = new FoodItem();
                        newFoodItem.setName(mealRequestDTO.getFoodItemName());
                        return foodItemRepository.save(newFoodItem);
                    });
            meal.setFoodItem(foodItem);
        }

        // Get current participants
        Set<String> currentPins = participationRepository.findByMealId(meal.getId())
                .stream()
                .map(p -> p.getEmployee().getPin())
                .collect(Collectors.toSet());

        Set<String> newPins = new HashSet<>(mealRequestDTO.getParticipants());

        // Find participants to add
        Set<String> pinsToAdd = new HashSet<>(newPins);
        pinsToAdd.removeAll(currentPins);

        // Find participants to remove
        Set<String> pinsToRemove = new HashSet<>(currentPins);
        pinsToRemove.removeAll(newPins);

        // Process adds
        List<TransactionResponseDTO> newTransactions = new ArrayList<>();
        for (String pin : pinsToAdd) {
            Employee employee = employeeRepository.findByPin(pin)
                    .orElseThrow(() -> new RuntimeException("Employee not found with PIN: " + pin));

            createParticipation(meal, employee);

            // Create debit transaction
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setEmployeeId(employee.getId());
            transactionDTO.setMealId(meal.getId());
            transactionDTO.setAmount(meal.getPerHeadCost().negate());
            transactionDTO.setTransactionDate(LocalDate.now());

            newTransactions.add(transactionService.createTransaction(transactionDTO));
        }

        // Process removals
        for (String pin : pinsToRemove) {
            Employee employee = employeeRepository.findByPin(pin)
                    .orElseThrow(() -> new RuntimeException("Employee not found with PIN: " + pin));

            participationRepository.deleteByMealIdAndEmployeeId(meal.getId(), employee.getId());

            // Create credit transaction (refund)
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setEmployeeId(employee.getId());
            transactionDTO.setMealId(meal.getId());
            transactionDTO.setAmount(meal.getPerHeadCost());
            transactionDTO.setTransactionDate(LocalDate.now());

            newTransactions.add(transactionService.createTransaction(transactionDTO));
        }

        // Update participant count and per head cost
        int newParticipantCount = newPins.size();
        meal.setTotalParticipants(newParticipantCount);

        BigDecimal newPerHeadCost = newParticipantCount > 0 ?
                meal.getTotalCost().divide(BigDecimal.valueOf(newParticipantCount), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        meal.setPerHeadCost(newPerHeadCost);

        Meal updatedMeal = mealRepository.save(meal);

        // Get all transactions for this meal
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByMeal(meal.getId());
        transactions.addAll(newTransactions);

        return buildMealResponse(updatedMeal, transactions);
    }

    @Override
    @Transactional
    public void deleteMeal(Long id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found with id: " + id));

        // Refund all participants
        List<MealParticipation> participations = participationRepository.findByMealId(meal.getId());
        for (MealParticipation participation : participations) {
            // Create credit transaction (refund)
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setEmployeeId(participation.getEmployee().getId());
            transactionDTO.setMealId(meal.getId());
            transactionDTO.setAmount(meal.getPerHeadCost());
            transactionDTO.setTransactionDate(LocalDate.now());

            transactionService.createTransaction(transactionDTO);
        }

        mealRepository.delete(meal);
    }

    @Override
    @Transactional
    public MealResponseDTO toggleParticipant(Long mealId, String pin, boolean isParticipating) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found with id: " + mealId));

        Employee employee = employeeRepository.findByPin(pin)
                .orElseThrow(() -> new RuntimeException("Employee not found with PIN: " + pin));

        Optional<MealParticipation> participation = participationRepository.findByMealIdAndEmployeeId(mealId, employee.getId());

        if (isParticipating) {
            if (participation.isEmpty()) {
                // Add participant
                createParticipation(meal, employee);

                // Create debit transaction
                TransactionDTO transactionDTO = new TransactionDTO();
                transactionDTO.setEmployeeId(employee.getId());
                transactionDTO.setMealId(meal.getId());
                transactionDTO.setAmount(meal.getPerHeadCost().negate());
                transactionDTO.setTransactionDate(LocalDate.now());

                transactionService.createTransaction(transactionDTO);

                // Update meal counts
                meal.setTotalParticipants(meal.getTotalParticipants() + 1);
                recalculatePerHeadCost(meal);
            }
        } else {
            if (participation.isPresent()) {
                // Remove participant
                participationRepository.delete(participation.get());

                // Create credit transaction (refund)
                TransactionDTO transactionDTO = new TransactionDTO();
                transactionDTO.setEmployeeId(employee.getId());
                transactionDTO.setMealId(meal.getId());
                transactionDTO.setAmount(meal.getPerHeadCost());
                transactionDTO.setTransactionDate(LocalDate.now());

                transactionService.createTransaction(transactionDTO);

                // Update meal counts
                meal.setTotalParticipants(meal.getTotalParticipants() - 1);
                recalculatePerHeadCost(meal);
            }
        }

        meal = mealRepository.save(meal);
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByMeal(mealId);
        return buildMealResponse(meal, transactions);
    }

    @Override
    public MealResponseDTO getMealById(Long id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found with id: " + id));

        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByMeal(id);
        return buildMealResponse(meal, transactions);
    }

    @Override
    public List<MealResponseDTO> getAllMeals() {
        return mealRepository.findAll().stream()
                .map(meal -> {
                    List<TransactionResponseDTO> transactions = transactionService.getTransactionsByMeal(meal.getId());
                    return buildMealResponse(meal, transactions);
                })
                .collect(Collectors.toList());
    }

    // Helper methods
    private void createParticipation(Meal meal, Employee employee) {
        MealParticipation participation = new MealParticipation();
        participation.setMeal(meal);
        participation.setEmployee(employee);
        participation.setParticipationDate(meal.getMealDate());
        participationRepository.save(participation);
    }

    private void recalculatePerHeadCost(Meal meal) {
        if (meal.getTotalParticipants() > 0) {
            BigDecimal newPerHeadCost = meal.getTotalCost()
                    .divide(BigDecimal.valueOf(meal.getTotalParticipants()), 2, RoundingMode.HALF_UP);
            meal.setPerHeadCost(newPerHeadCost);
        } else {
            meal.setPerHeadCost(BigDecimal.ZERO);
        }
    }

    private MealResponseDTO buildMealResponse(Meal meal, List<TransactionResponseDTO> transactions) {
        MealResponseDTO response = modelMapper.map(meal, MealResponseDTO.class);
        response.setFoodItemName(meal.getFoodItem().getName());

        // Get participant PINs
        List<String> pins = participationRepository.findByMealId(meal.getId())
                .stream()
                .map(p -> p.getEmployee().getPin())
                .collect(Collectors.toList());

        response.setParticipants(pins);
        response.setTransactions(transactions);
        return response;
    }

    @Override
    @Transactional
    public MealResponseDTO toggleParticipant(MealPinOperationDTO operation) {
        Meal meal = mealRepository.findById(operation.getMealId())
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        Employee employee = employeeRepository.findByPin(operation.getPin())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Optional<MealParticipation> participation = participationRepository
                .findByMealIdAndEmployeeId(meal.getId(), employee.getId());

        if (operation.isParticipate()) {
            if (participation.isEmpty()) {
                addParticipant(meal, employee);
            }
        } else {
            if (participation.isPresent()) {
                removeParticipant(meal, participation.get());
            }
        }

        // Fetch transactions and convert to DTOs
        List<Transaction> transactions = transactionRepository.findByMealId(meal.getId());
        List<TransactionResponseDTO> transactionDTOs = transactions.stream()
                .map(this::convertToTransactionResponseDTO)
                .collect(Collectors.toList());

        return buildMealResponse(meal, transactionDTOs);
    }

    // Helper method to convert Transaction to TransactionResponseDTO
    private TransactionResponseDTO convertToTransactionResponseDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setDescription(transaction.getDescription());
        // Set other necessary fields
        return dto;
    }

    private void addParticipant(Meal meal, Employee employee) {
        // Create participation record
        MealParticipation participation = new MealParticipation();
        participation.setMeal(meal);
        participation.setEmployee(employee);
        participation.setParticipationDate(LocalDate.now());
        participationRepository.save(participation);

        // Deduct from employee balance
        BigDecimal perHeadCost = meal.getPerHeadCost();
        employee.setBalance(employee.getBalance().subtract(perHeadCost));
        employeeRepository.save(employee);

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setEmployee(employee);
        transaction.setMeal(meal);
        transaction.setAmount(perHeadCost.negate());
        transaction.setTransactionDate(LocalDate.now());
        transaction.setDescription("Meal deduction: " + meal.getFoodItem().getName());
        transactionRepository.save(transaction);

        // Update meal counts
        meal.setTotalParticipants(meal.getTotalParticipants() + 1);
        mealRepository.save(meal);
    }

    private void removeParticipant(Meal meal, MealParticipation participation) {
        // Refund to employee balance
        BigDecimal perHeadCost = meal.getPerHeadCost();
        Employee employee = participation.getEmployee();
        employee.setBalance(employee.getBalance().add(perHeadCost));
        employeeRepository.save(employee);

        // Create refund transaction
        Transaction transaction = new Transaction();
        transaction.setEmployee(employee);
        transaction.setMeal(meal);
        transaction.setAmount(perHeadCost);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setDescription("Meal refund: " + meal.getFoodItem().getName());
        transactionRepository.save(transaction);

        // Remove participation
        participationRepository.delete(participation);

        // Update meal counts
        meal.setTotalParticipants(meal.getTotalParticipants() - 1);
        mealRepository.save(meal);
    }

    @Override
    @Transactional
    public List<MealSummaryDTO> processBulkMeals(BulkMealRequestDTO bulkRequest) {
        List<MealSummaryDTO> results = new ArrayList<>();

        for (MealRequestDTO mealDTO : bulkRequest.getMeals()) {
            Meal meal = createOrUpdateMeal(mealDTO);
            results.add(mapToSummaryDTO(meal));
        }

        return results;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long processDailyMeal(DailyMealDTO dailyMeal) {
        // Find or create food item
        FoodItem foodItem = foodItemRepository.findByName(dailyMeal.getFoodItemName())
                .orElseGet(() -> {
                    FoodItem newItem = new FoodItem();
                    newItem.setName(dailyMeal.getFoodItemName());
                    return foodItemRepository.save(newItem);
                });

        // Calculate per-head cost
        int participantCount = dailyMeal.getParticipants().size();
        BigDecimal perHeadCost = participantCount > 0 ?
                dailyMeal.getTotalCost().divide(BigDecimal.valueOf(participantCount), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        // Create meal
        Meal meal = new Meal();
        meal.setMealDate(dailyMeal.getDate());
        meal.setFoodItem(foodItem);
        meal.setPerHeadCost(perHeadCost);
        meal.setTotalParticipants(participantCount);
        meal.setTotalCost(dailyMeal.getTotalCost());
        meal.setCreatedAt(LocalDateTime.now());
        meal.setUpdatedAt(LocalDateTime.now());
        meal = mealRepository.save(meal);

        // Process participants
        Set<String> uniquePins = new HashSet<>(dailyMeal.getParticipants());
        for (String pin : uniquePins) {
            Employee employee = employeeRepository.findByPin(pin)
                    .orElseThrow(() -> new RuntimeException("Employee not found: " + pin));

            // Create participation
            MealParticipation participation = new MealParticipation();
            participation.setMeal(meal);
            participation.setEmployee(employee);
            participation.setParticipationDate(dailyMeal.getDate());
            participationRepository.save(participation);

            // Update balance
            employee.setBalance(employee.getBalance().subtract(perHeadCost));
            employeeRepository.save(employee);

            // Create transaction
            Transaction transaction = new Transaction();
            transaction.setEmployee(employee);
            transaction.setMeal(meal);
            transaction.setAmount(perHeadCost.negate());
            transaction.setTransactionDate(dailyMeal.getDate());
            transaction.setDescription("Meal: " + foodItem.getName());
            transactionRepository.save(transaction);
        }

        return meal.getId();
    }

    public List<DailyMealResponseDTO> processBulkMealsV2(List<DailyMealDTO> bulkRequest) {
        List<DailyMealResponseDTO> responses = new ArrayList<>();
        for (DailyMealDTO dailyMeal : bulkRequest) {
            DailyMealResponseDTO response = new DailyMealResponseDTO();
            response.setDate(dailyMeal.getDate());

            try {
                Long mealId = processDailyMeal(dailyMeal);
                response.setStatus("SUCCESS");
                response.setMealId(mealId);
            } catch (Exception e) {
                response.setStatus("ERROR");
                response.setMessage(e.getMessage());
            }
            responses.add(response);
        }
        return responses;
    }

    private Meal createOrUpdateMeal(MealRequestDTO mealDTO) {
        // Check if meal exists for this date
        Optional<Meal> existingMeal = mealRepository.findByMealDate(mealDTO.getMealDate());

        if (existingMeal.isPresent()) {
            return updateMeal(existingMeal.get(), mealDTO);
        } else {
            return createNewMeal(mealDTO);
        }
    }

    private Meal createNewMeal(MealRequestDTO mealDTO) {
        FoodItem foodItem = foodItemRepository.findByName(mealDTO.getFoodItemName())
                .orElseGet(() -> createFoodItem(mealDTO.getFoodItemName()));

        Meal meal = new Meal();
        meal.setMealDate(mealDTO.getMealDate());
        meal.setFoodItem(foodItem);
        meal.setTotalCost(mealDTO.getTotalCost());
        meal.setIsTotalCostFixed(true);
        meal.setCreatedAt(LocalDateTime.now());
        meal.setUpdatedAt(LocalDateTime.now());

        // Process participants
        int participantCount = mealDTO.getParticipants().size();
        meal.setTotalParticipants(participantCount);

        BigDecimal perHeadCost = participantCount > 0 ?
                mealDTO.getTotalCost().divide(BigDecimal.valueOf(participantCount), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        meal.setPerHeadCost(perHeadCost);
        meal = mealRepository.save(meal);

        // Process participants
        for (String pin : mealDTO.getParticipants()) {
            Employee employee = employeeRepository.findByPin(pin)
                    .orElseThrow(() -> new RuntimeException("Employee not found: " + pin));
            addParticipant(meal, employee);
        }

        return meal;
    }

    private Meal updateMeal(Meal meal, MealRequestDTO mealDTO) {
        // Update basic info
        meal.setTotalCost(mealDTO.getTotalCost());
        meal.setUpdatedAt(LocalDateTime.now());

        // Update food item if changed
        if (!meal.getFoodItem().getName().equals(mealDTO.getFoodItemName())) {
            FoodItem foodItem = foodItemRepository.findByName(mealDTO.getFoodItemName())
                    .orElseGet(() -> createFoodItem(mealDTO.getFoodItemName()));
            meal.setFoodItem(foodItem);
        }

        // Get current participants
        Set<String> currentPins = participationRepository.findByMealId(meal.getId())
                .stream()
                .map(p -> p.getEmployee().getPin())
                .collect(Collectors.toSet());

        Set<String> newPins = new HashSet<>(mealDTO.getParticipants());

        // Add missing participants
        for (String pin : newPins) {
            if (!currentPins.contains(pin)) {
                Employee employee = employeeRepository.findByPin(pin)
                        .orElseThrow(() -> new RuntimeException("Employee not found: " + pin));
                addParticipant(meal, employee);
            }
        }

        // Remove extra participants
        for (String pin : currentPins) {
            if (!newPins.contains(pin)) {
                MealParticipation participation = participationRepository
                        .findByMealIdAndEmployeePin(meal.getId(), pin)
                        .orElseThrow(() -> new RuntimeException("Participation not found"));
                removeParticipant(meal, participation);
            }
        }

        return mealRepository.save(meal);
    }

    private FoodItem createFoodItem(String name) {
        FoodItem foodItem = new FoodItem();
        foodItem.setName(name);
        return foodItemRepository.save(foodItem);
    }

    private MealSummaryDTO mapToSummaryDTO(Meal meal) {
        MealSummaryDTO dto = new MealSummaryDTO();
        dto.setDate(meal.getMealDate());
        dto.setFoodItemName(meal.getFoodItem().getName());
        dto.setTotalCost(meal.getTotalCost());
        dto.setParticipantCount(meal.getTotalParticipants());
        dto.setPerHeadCost(meal.getPerHeadCost());
        return dto;
    }
}