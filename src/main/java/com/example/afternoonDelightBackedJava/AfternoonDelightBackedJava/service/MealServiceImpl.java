package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.*;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j  // This adds the logger automatically
public class MealServiceImpl implements MealService {

    private final MealRepository mealRepository;
    private final FoodItemRepository foodItemRepository;
    private final TransactionRepository transactionRepository;
    private final EmployeeRepository employeeRepository;
    private final MealParticipationRepository participationRepository;
    private final TransactionService transactionService;
    private final ModelMapper modelMapper;

    @Transactional
    public Meal createMeal(MealCreationDTO mealDTO) {
        log.info("Creating meal for date: {} with food item: {}",
                mealDTO.getMealDate(), mealDTO.getFoodItem());

        // Fetch the FoodItem entity by name/id
        FoodItem foodItem = foodItemRepository.findByName(mealDTO.getFoodItem())
                .orElseThrow(() -> new RuntimeException("Food item not found: " + mealDTO.getFoodItem()));

        // Fetch the organizer (creator) if organizerPin is provided
        Employee organizer = null;
        if (mealDTO.getOrganizerPin() != null && !mealDTO.getOrganizerPin().isEmpty()) {
            organizer = employeeRepository.findByPin(mealDTO.getOrganizerPin())
                    .orElseThrow(() -> new RuntimeException("Organizer not found with PIN: " + mealDTO.getOrganizerPin()));
        }

        // Create the meal entity
        Meal meal = new Meal();
        meal.setMealDate(mealDTO.getMealDate());
        meal.setFoodItem(foodItem);
        // Convert double to BigDecimal
        meal.setTotalCost(mealDTO.getTotalCost());
        meal.setIsTotalCostFixed(true); // Assuming total cost is fixed when provided
        meal.setCreatedAt(LocalDateTime.now());
        meal.setUpdatedAt(LocalDateTime.now());
        meal.setCreatedBy(organizer);

        // Set total participants count and calculate per head cost
        if (mealDTO.getParticipants() != null && !mealDTO.getParticipants().isEmpty()) {
            meal.setTotalParticipants(mealDTO.getParticipants().size());
            // Calculate per head cost
            BigDecimal perHeadCost = (mealDTO.getTotalCost())
                    .divide(BigDecimal.valueOf(mealDTO.getParticipants().size()), 2, RoundingMode.HALF_UP);
            meal.setPerHeadCost(perHeadCost);
        } else {
            meal.setTotalParticipants(0);
            meal.setPerHeadCost(BigDecimal.ZERO);
        }

        // Save the meal first to get the ID
        Meal savedMeal = mealRepository.save(meal);
        log.info("Meal saved with ID: {}", savedMeal.getId());

        // Create MealParticipation records for each participant
        if (mealDTO.getParticipants() != null && !mealDTO.getParticipants().isEmpty()) {
            List<MealParticipation> participations = new ArrayList<>();

            for (String participantPin : mealDTO.getParticipants()) {
                // Fetch employee by PIN
                Employee participant = employeeRepository.findByPin(participantPin)
                        .orElseThrow(() -> new RuntimeException("Employee not found with PIN: " + participantPin));

                MealParticipation participation = new MealParticipation();
                participation.setMeal(savedMeal);
                participation.setEmployee(participant);
                participation.setParticipationDate(mealDTO.getMealDate());
                participation.setCreatedBy(organizer);

                participations.add(participation);
            }

            // Save all participations
            List<MealParticipation> savedParticipations = participationRepository.saveAll(participations);
            log.info("Created {} meal participation records", savedParticipations.size());

            // Set the participations back to the meal (optional, for immediate access)
            savedMeal.setParticipations(savedParticipations);
        }

        log.info("Successfully created meal with {} participants",
                savedMeal.getTotalParticipants());

        return savedMeal;
    }
    @Override
    public MealResponseDTO getMealByDate(LocalDate date) {
        log.info("Fetching meal for date: {}", date);

        try {
            // Use the eager loading version to avoid lazy loading issues
            Optional<Meal> mealOpt = mealRepository.findByMealDateWithParticipations(date);

            if (mealOpt.isEmpty()) {
                log.warn("No meal found for date: {}", date);
                throw new RuntimeException("No meal found for date: " + date);
            }

            Meal meal = mealOpt.get();

            MealResponseDTO response = new MealResponseDTO();
            response.setMealDate(meal.getMealDate());
            response.setFoodItemName(meal.getFoodItem().getName());
            response.setTotalCost(meal.getTotalCost());
            response.setPerHeadCost(meal.getPerHeadCost());
            response.setTotalParticipants(meal.getTotalParticipants());

            // Get participant PINs - participations should be loaded due to eager fetch
            List<String> participantPins = meal.getParticipations() != null ?
                    meal.getParticipations().stream()
                            .map(mp -> mp.getEmployee().getPin())
                            .collect(Collectors.toList()) :
                    new ArrayList<>();

            response.setParticipants(participantPins);

            log.info("Successfully fetched meal for date: {} with {} participants", date, participantPins.size());
            return response;

        } catch (Exception e) {
            log.error("Error fetching meal for date: {}", date, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public MealResponseDTO updateMeal(Long id, MealRequestDTO mealRequestDTO) {
        log.info("Updating meal with ID: {}", id);

        try {
            Meal meal = mealRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Meal not found with id: {}", id);
                        return new RuntimeException("Meal not found with id: " + id);
                    });

            log.debug("Current meal participants: {}", meal.getTotalParticipants());

            // Update basic info
            meal.setMealDate(mealRequestDTO.getMealDate());
            meal.setTotalCost(mealRequestDTO.getTotalCost());
            meal.setUpdatedAt(LocalDateTime.now());

            // Update food item if changed
            if (!meal.getFoodItem().getName().equals(mealRequestDTO.getFoodItemName())) {
                log.info("Updating food item from '{}' to '{}'",
                        meal.getFoodItem().getName(), mealRequestDTO.getFoodItemName());

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
            log.info("Adding {} new participants", pinsToAdd.size());

            // Find participants to remove
            Set<String> pinsToRemove = new HashSet<>(currentPins);
            pinsToRemove.removeAll(newPins);
            log.info("Removing {} participants", pinsToRemove.size());

            // Process adds
            List<TransactionResponseDTO> newTransactions = new ArrayList<>();
            for (String pin : pinsToAdd) {
                try {
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
                    log.debug("Added participant and created transaction for PIN: {}", pin);

                } catch (Exception e) {
                    log.error("Error adding participant with PIN: {}", pin, e);
                    throw e;
                }
            }

            // Process removals
            for (String pin : pinsToRemove) {
                try {
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
                    log.debug("Removed participant and created refund transaction for PIN: {}", pin);

                } catch (Exception e) {
                    log.error("Error removing participant with PIN: {}", pin, e);
                    throw e;
                }
            }

            // Update participant count and per head cost
            int newParticipantCount = newPins.size();
            meal.setTotalParticipants(newParticipantCount);

            BigDecimal newPerHeadCost = newParticipantCount > 0 ?
                    meal.getTotalCost().divide(BigDecimal.valueOf(newParticipantCount), 2, RoundingMode.HALF_UP) :
                    BigDecimal.ZERO;

            meal.setPerHeadCost(newPerHeadCost);
            log.debug("Updated per head cost to: {} for {} participants", newPerHeadCost, newParticipantCount);

            Meal updatedMeal = mealRepository.save(meal);

            // Get all transactions for this meal
            List<TransactionResponseDTO> transactions = transactionService.getTransactionsByMeal(meal.getId());
            transactions.addAll(newTransactions);

            log.info("Successfully updated meal with ID: {}", id);
            return buildMealResponse(updatedMeal, transactions);

        } catch (Exception e) {
            log.error("Error updating meal with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteMeal(Long id) {
        log.info("Deleting meal with ID: {}", id);

        try {
            Meal meal = mealRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Meal not found with id: {}", id);
                        return new RuntimeException("Meal not found with id: " + id);
                    });

            // Refund all participants
            List<MealParticipation> participations = participationRepository.findByMealId(meal.getId());
            log.info("Refunding {} participants for deleted meal", participations.size());

            for (MealParticipation participation : participations) {
                try {
                    // Create credit transaction (refund)
                    TransactionDTO transactionDTO = new TransactionDTO();
                    transactionDTO.setEmployeeId(participation.getEmployee().getId());
                    transactionDTO.setMealId(meal.getId());
                    transactionDTO.setAmount(meal.getPerHeadCost());
                    transactionDTO.setTransactionDate(LocalDate.now());

                    transactionService.createTransaction(transactionDTO);
                    log.debug("Created refund transaction for employee PIN: {}",
                            participation.getEmployee().getPin());

                } catch (Exception e) {
                    log.error("Error creating refund transaction for employee PIN: {}",
                            participation.getEmployee().getPin(), e);
                    throw e;
                }
            }

            mealRepository.delete(meal);
            log.info("Successfully deleted meal with ID: {}", id);

        } catch (Exception e) {
            log.error("Error deleting meal with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public MealResponseDTO toggleParticipant(Long mealId, String pin, boolean isParticipating) {
        log.info("Toggling participant with PIN: {} for meal ID: {}, participating: {}",
                pin, mealId, isParticipating);

        try {
            Meal meal = mealRepository.findById(mealId)
                    .orElseThrow(() -> {
                        log.error("Meal not found with id: {}", mealId);
                        return new RuntimeException("Meal not found with id: " + mealId);
                    });

            Employee employee = employeeRepository.findByPin(pin)
                    .orElseThrow(() -> {
                        log.error("Employee not found with PIN: {}", pin);
                        return new RuntimeException("Employee not found with PIN: " + pin);
                    });

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

                    log.info("Added participant with PIN: {} to meal ID: {}", pin, mealId);
                } else {
                    log.debug("Employee with PIN: {} is already participating in meal ID: {}", pin, mealId);
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

                    log.info("Removed participant with PIN: {} from meal ID: {}", pin, mealId);
                } else {
                    log.debug("Employee with PIN: {} is not participating in meal ID: {}", pin, mealId);
                }
            }

            meal = mealRepository.save(meal);
            List<TransactionResponseDTO> transactions = transactionService.getTransactionsByMeal(mealId);

            log.info("Successfully toggled participant with PIN: {} for meal ID: {}", pin, mealId);
            return buildMealResponse(meal, transactions);

        } catch (Exception e) {
            log.error("Error toggling participant with PIN: {} for meal ID: {}", pin, mealId, e);
            throw e;
        }
    }

    @Override
    public MealResponseDTO getMealById(Long id) {
        log.info("Fetching meal by ID: {}", id);

        try {
            Meal meal = mealRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Meal not found with id: {}", id);
                        return new RuntimeException("Meal not found with id: " + id);
                    });

            List<TransactionResponseDTO> transactions = transactionService.getTransactionsByMeal(id);
            log.info("Successfully fetched meal by ID: {} with {} transactions", id, transactions.size());
            return buildMealResponse(meal, transactions);

        } catch (Exception e) {
            log.error("Error fetching meal by ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<MealResponseDTO> getAllMeals() {
        log.info("Fetching all meals");

        try {
            List<MealResponseDTO> meals = mealRepository.findAll().stream()
                    .map(meal -> {
                        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByMeal(meal.getId());
                        return buildMealResponse(meal, transactions);
                    })
                    .collect(Collectors.toList());

            log.info("Successfully fetched {} meals", meals.size());
            return meals;

        } catch (Exception e) {
            log.error("Error fetching all meals", e);
            throw e;
        }
    }

    // Helper methods with logging
    private void createParticipation(Meal meal, Employee employee) {
        log.debug("Creating participation for employee PIN: {} in meal ID: {}",
                employee.getPin(), meal.getId());

        try {
            MealParticipation participation = new MealParticipation();
            participation.setMeal(meal);
            participation.setEmployee(employee);
            participation.setParticipationDate(meal.getMealDate());
            participationRepository.save(participation);

            log.debug("Successfully created participation for employee PIN: {}", employee.getPin());

        } catch (Exception e) {
            log.error("Error creating participation for employee PIN: {} in meal ID: {}",
                    employee.getPin(), meal.getId(), e);
            throw e;
        }
    }

    private void recalculatePerHeadCost(Meal meal) {
        log.debug("Recalculating per head cost for meal ID: {}", meal.getId());

        BigDecimal oldPerHeadCost = meal.getPerHeadCost();

        if (meal.getTotalParticipants() > 0) {
            BigDecimal newPerHeadCost = meal.getTotalCost()
                    .divide(BigDecimal.valueOf(meal.getTotalParticipants()), 2, RoundingMode.HALF_UP);
            meal.setPerHeadCost(newPerHeadCost);
            log.debug("Per head cost updated from {} to {} for meal ID: {}",
                    oldPerHeadCost, newPerHeadCost, meal.getId());
        } else {
            meal.setPerHeadCost(BigDecimal.ZERO);
            log.debug("Per head cost set to 0 (no participants) for meal ID: {}", meal.getId());
        }
    }

    private MealResponseDTO buildMealResponse(Meal meal, List<TransactionResponseDTO> transactions) {
        log.debug("Building meal response for meal ID: {}", meal.getId());

        try {
            MealResponseDTO response = modelMapper.map(meal, MealResponseDTO.class);
            response.setFoodItemName(meal.getFoodItem().getName());

            // Get participant PINs
            List<String> pins = participationRepository.findByMealId(meal.getId())
                    .stream()
                    .map(p -> p.getEmployee().getPin())
                    .collect(Collectors.toList());

            response.setParticipants(pins);
            response.setTransactions(transactions);

            log.debug("Successfully built meal response for meal ID: {} with {} participants and {} transactions",
                    meal.getId(), pins.size(), transactions.size());
            return response;

        } catch (Exception e) {
            log.error("Error building meal response for meal ID: {}", meal.getId(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public MealResponseDTO toggleParticipant(MealPinOperationDTO operation) {
        log.info("Processing meal pin operation for meal ID: {}, PIN: {}, participate: {}",
                operation.getMealId(), operation.getPin(), operation.isParticipate());

        try {
            Meal meal = mealRepository.findById(operation.getMealId())
                    .orElseThrow(() -> {
                        log.error("Meal not found with ID: {}", operation.getMealId());
                        return new RuntimeException("Meal not found");
                    });

            Employee employee = employeeRepository.findByPin(operation.getPin())
                    .orElseThrow(() -> {
                        log.error("Employee not found with PIN: {}", operation.getPin());
                        return new RuntimeException("Employee not found");
                    });

            Optional<MealParticipation> participation = participationRepository
                    .findByMealIdAndEmployeeId(meal.getId(), employee.getId());

            if (operation.isParticipate()) {
                if (participation.isEmpty()) {
                    addParticipant(meal, employee);
                    log.info("Added participant with PIN: {} to meal ID: {}",
                            operation.getPin(), operation.getMealId());
                }
            } else {
                if (participation.isPresent()) {
                    removeParticipant(meal, participation.get());
                    log.info("Removed participant with PIN: {} from meal ID: {}",
                            operation.getPin(), operation.getMealId());
                }
            }

            // Fetch transactions and convert to DTOs
            List<Transaction> transactions = transactionRepository.findByMealId(meal.getId());
            List<TransactionResponseDTO> transactionDTOs = transactions.stream()
                    .map(this::convertToTransactionResponseDTO)
                    .collect(Collectors.toList());

            log.info("Successfully processed meal pin operation for meal ID: {}", operation.getMealId());
            return buildMealResponse(meal, transactionDTOs);

        } catch (Exception e) {
            log.error("Error processing meal pin operation for meal ID: {}, PIN: {}",
                    operation.getMealId(), operation.getPin(), e);
            throw e;
        }
    }

    // Helper method to convert Transaction to TransactionResponseDTO
    private TransactionResponseDTO convertToTransactionResponseDTO(Transaction transaction) {
        log.debug("Converting transaction ID: {} to DTO", transaction.getId());

        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setDescription(transaction.getDescription());
        // Set other necessary fields
        return dto;
    }

    private void addParticipant(Meal meal, Employee employee) {
        log.info("Adding participant with PIN: {} to meal ID: {}", employee.getPin(), meal.getId());

        try {
            // Create participation record
            MealParticipation participation = new MealParticipation();
            participation.setMeal(meal);
            participation.setEmployee(employee);
            participation.setParticipationDate(LocalDate.now());
            participationRepository.save(participation);

            // Deduct from employee balance
            BigDecimal perHeadCost = meal.getPerHeadCost();
            BigDecimal oldBalance = employee.getBalance();
            employee.setBalance(employee.getBalance().subtract(perHeadCost));
            employeeRepository.save(employee);

            log.debug("Updated employee balance from {} to {} for PIN: {}",
                    oldBalance, employee.getBalance(), employee.getPin());

            // Create transaction
            Transaction transaction = new Transaction();
            transaction.setEmployee(employee);
            transaction.setMeal(meal);
            transaction.setAmount(perHeadCost.negate());
            transaction.setTransactionDate(LocalDate.now());
            transaction.setDescription("Meal deduction: " + meal.getFoodItem().getName());
            transactionRepository.save(transaction);

            // Update meal counts
            int oldParticipantCount = meal.getTotalParticipants();
            meal.setTotalParticipants(meal.getTotalParticipants() + 1);
            mealRepository.save(meal);

            log.info("Successfully added participant with PIN: {} to meal ID: {}. Participant count: {} -> {}",
                    employee.getPin(), meal.getId(), oldParticipantCount, meal.getTotalParticipants());

        } catch (Exception e) {
            log.error("Error adding participant with PIN: {} to meal ID: {}",
                    employee.getPin(), meal.getId(), e);
            throw e;
        }
    }

    private void removeParticipant(Meal meal, MealParticipation participation) {
        Employee employee = participation.getEmployee();
        log.info("Removing participant with PIN: {} from meal ID: {}", employee.getPin(), meal.getId());

        try {
            // Refund to employee balance
            BigDecimal perHeadCost = meal.getPerHeadCost();
            BigDecimal oldBalance = employee.getBalance();
            employee.setBalance(employee.getBalance().add(perHeadCost));
            employeeRepository.save(employee);

            log.debug("Refunded employee balance from {} to {} for PIN: {}",
                    oldBalance, employee.getBalance(), employee.getPin());

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
            int oldParticipantCount = meal.getTotalParticipants();
            meal.setTotalParticipants(meal.getTotalParticipants() - 1);
            mealRepository.save(meal);

            log.info("Successfully removed participant with PIN: {} from meal ID: {}. Participant count: {} -> {}",
                    employee.getPin(), meal.getId(), oldParticipantCount, meal.getTotalParticipants());

        } catch (Exception e) {
            log.error("Error removing participant with PIN: {} from meal ID: {}",
                    employee.getPin(), meal.getId(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public List<MealSummaryDTO> processBulkMeals(BulkMealRequestDTO bulkRequest) {
        log.info("Processing bulk meals with {} items", bulkRequest.getMeals().size());

        List<MealSummaryDTO> results = new ArrayList<>();

        for (MealRequestDTO mealDTO : bulkRequest.getMeals()) {
            try {
                Meal meal = createOrUpdateMeal(mealDTO);
                results.add(mapToSummaryDTO(meal));
                log.debug("Successfully processed bulk meal for date: {}", mealDTO.getMealDate());
            } catch (Exception e) {
                log.error("Error processing bulk meal for date: {}", mealDTO.getMealDate(), e);
                throw e;
            }
        }

        log.info("Successfully processed {} bulk meals", results.size());
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long processDailyMeal(DailyMealDTO dailyMeal) {
        log.info("Processing daily meal for date: {} with food item: {}",
                dailyMeal.getDate(), dailyMeal.getFoodItemName());

        try {
            // Find or create food item
            FoodItem foodItem = foodItemRepository.findByName(dailyMeal.getFoodItemName())
                    .orElseGet(() -> {
                        log.info("Creating new food item: {}", dailyMeal.getFoodItemName());
                        FoodItem newItem = new FoodItem();
                        newItem.setName(dailyMeal.getFoodItemName());
                        return foodItemRepository.save(newItem);
                    });

            // Calculate per-head cost
            int participantCount = dailyMeal.getParticipants().size();
            BigDecimal perHeadCost = participantCount > 0 ?
                    dailyMeal.getTotalCost().divide(BigDecimal.valueOf(participantCount), 2, RoundingMode.HALF_UP) :
                    BigDecimal.ZERO;

            log.debug("Calculated per head cost: {} for {} participants", perHeadCost, participantCount);

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

            log.info("Created meal with ID: {} for date: {}", meal.getId(), dailyMeal.getDate());

            // Process participants
            Set<String> uniquePins = new HashSet<>(dailyMeal.getParticipants());
            for (String pin : uniquePins) {
                try {
                    Employee employee = employeeRepository.findByPin(pin)
                            .orElseThrow(() -> new RuntimeException("Employee not found: " + pin));

                    // Create participation
                    MealParticipation participation = new MealParticipation();
                    participation.setMeal(meal);
                    participation.setEmployee(employee);
                    participation.setParticipationDate(dailyMeal.getDate());
                    participationRepository.save(participation);

                    // Update balance
                    BigDecimal oldBalance = employee.getBalance();
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

                    log.debug("Processed participant PIN: {}, balance: {} -> {}",
                            pin, oldBalance, employee.getBalance());

                } catch (Exception e) {
                    log.error("Error processing participant PIN: {} for daily meal on date: {}",
                            pin, dailyMeal.getDate(), e);
                    throw e;
                }
            }

            log.info("Successfully processed daily meal with ID: {} for date: {} with {} participants",
                    meal.getId(), dailyMeal.getDate(), uniquePins.size());
            return meal.getId();

        } catch (Exception e) {
            log.error("Error processing daily meal for date: {} with food item: {}",
                    dailyMeal.getDate(), dailyMeal.getFoodItemName(), e);
            throw e;
        }
    }

    public List<DailyMealResponseDTO> processBulkMealsV2(List<DailyMealDTO> bulkRequest) {
        log.info("Processing bulk meals V2 with {} items", bulkRequest.size());

        List<DailyMealResponseDTO> responses = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        for (DailyMealDTO dailyMeal : bulkRequest) {
            DailyMealResponseDTO response = new DailyMealResponseDTO();
            response.setDate(dailyMeal.getDate());

            try {
                Long mealId = processDailyMeal(dailyMeal);
                response.setStatus("SUCCESS");
                response.setMealId(mealId);
                successCount++;
                log.debug("Successfully processed bulk meal V2 for date: {}", dailyMeal.getDate());
            } catch (Exception e) {
                response.setStatus("ERROR");
                response.setMessage(e.getMessage());
                errorCount++;
                log.error("Error processing bulk meal V2 for date: {}", dailyMeal.getDate(), e);
            }
            responses.add(response);
        }

        log.info("Completed bulk meals V2 processing. Success: {}, Errors: {}", successCount, errorCount);
        return responses;
    }

    private Meal createOrUpdateMeal(MealRequestDTO mealDTO) {
        log.debug("Creating or updating meal for date: {}", mealDTO.getMealDate());

        try {
            // Check if meal exists for this date
            Optional<Meal> existingMealOpt = mealRepository.findByMealDate(mealDTO.getMealDate());

            if (existingMealOpt.isPresent()) {
                Meal existingMeal = existingMealOpt.get();
                log.info("Updating existing meal ID: {} for date: {}", existingMeal.getId(), mealDTO.getMealDate());
                return updateMeal(existingMeal, mealDTO);
            } else {
                log.info("Creating new meal for date: {}", mealDTO.getMealDate());
                return createNewMeal(mealDTO);
            }
        } catch (Exception e) {
            log.error("Error in createOrUpdateMeal for date: {}", mealDTO.getMealDate(), e);
            throw e;
        }
    }
    private Meal createNewMeal(MealRequestDTO mealDTO) {
        log.debug("Creating new meal for date: {} with food item: {}",
                mealDTO.getMealDate(), mealDTO.getFoodItemName());

        try {
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

            log.info("Created new meal with ID: {} for date: {}", meal.getId(), mealDTO.getMealDate());

            // Process participants
            for (String pin : mealDTO.getParticipants()) {
                Employee employee = employeeRepository.findByPin(pin)
                        .orElseThrow(() -> {
                            log.error("Employee not found with PIN: {}", pin);
                            return new RuntimeException("Employee not found: " + pin);
                        });
                addParticipant(meal, employee);
            }

            log.info("Successfully created new meal with ID: {} and {} participants",
                    meal.getId(), participantCount);
            return meal;

        } catch (Exception e) {
            log.error("Error creating new meal for date: {}", mealDTO.getMealDate(), e);
            throw e;
        }
    }

    private Meal updateMeal(Meal meal, MealRequestDTO mealDTO) {
        log.debug("Updating existing meal ID: {} for date: {}", meal.getId(), mealDTO.getMealDate());

        try {
            // Update basic info
            meal.setTotalCost(mealDTO.getTotalCost());
            meal.setUpdatedAt(LocalDateTime.now());

            // Update food item if changed
            if (!meal.getFoodItem().getName().equals(mealDTO.getFoodItemName())) {
                log.info("Updating food item from '{}' to '{}' for meal ID: {}",
                        meal.getFoodItem().getName(), mealDTO.getFoodItemName(), meal.getId());

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
                            .orElseThrow(() -> {
                                log.error("Employee not found with PIN: {}", pin);
                                return new RuntimeException("Employee not found: " + pin);
                            });
                    addParticipant(meal, employee);
                }
            }

            // Remove extra participants
            for (String pin : currentPins) {
                if (!newPins.contains(pin)) {
                    MealParticipation participation = participationRepository
                            .findByMealIdAndEmployeePin(meal.getId(), pin)
                            .orElseThrow(() -> {
                                log.error("Participation not found for PIN: {} in meal ID: {}", pin, meal.getId());
                                return new RuntimeException("Participation not found");
                            });
                    removeParticipant(meal, participation);
                }
            }

            Meal updatedMeal = mealRepository.save(meal);
            log.info("Successfully updated meal ID: {} for date: {}", meal.getId(), mealDTO.getMealDate());
            return updatedMeal;

        } catch (Exception e) {
            log.error("Error updating meal ID: {} for date: {}", meal.getId(), mealDTO.getMealDate(), e);
            throw e;
        }
    }

    private FoodItem createFoodItem(String name) {
        log.info("Creating new food item: {}", name);

        try {
            FoodItem foodItem = new FoodItem();
            foodItem.setName(name);
            FoodItem savedItem = foodItemRepository.save(foodItem);

            log.info("Successfully created food item: {} with ID: {}", name, savedItem.getId());
            return savedItem;

        } catch (Exception e) {
            log.error("Error creating food item: {}", name, e);
            throw e;
        }
    }

    private MealSummaryDTO mapToSummaryDTO(Meal meal) {
        log.debug("Mapping meal ID: {} to summary DTO", meal.getId());

        MealSummaryDTO dto = new MealSummaryDTO();
        dto.setDate(meal.getMealDate());
        dto.setFoodItemName(meal.getFoodItem().getName());
        dto.setTotalCost(meal.getTotalCost());
        dto.setParticipantCount(meal.getTotalParticipants());
        dto.setPerHeadCost(meal.getPerHeadCost());
        return dto;
    }
}