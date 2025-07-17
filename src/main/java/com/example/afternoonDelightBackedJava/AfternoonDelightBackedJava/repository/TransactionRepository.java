package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Meal;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByMealId(Long mealId);

    List<Transaction> findByEmployeeId(Long employeeId);

    List<Transaction> findByEmployeeIdAndTransactionDate(Long employeeId, LocalDate date);

    List<Transaction> findByEmployeeAndTransactionDateBetween(
            Employee employee, LocalDate startDate, LocalDate endDate);

    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);

    Optional<Transaction> findByEmployeeAndMeal(Employee employee, Meal meal);

    List<Transaction> findByMeal(Meal meal);

    List<Transaction> findAllByMealId(Long mealId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.employee = :employee AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByEmployeeAndTransactionDateBetween(
            @Param("employee") Employee employee,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<Transaction> findByEmployeePinAndTransactionDateBetween(
            String pin,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("SELECT t FROM Transaction t WHERE t.employee.id = :employeeId AND YEAR(t.transactionDate) = :year AND MONTH(t.transactionDate) = :month ORDER BY t.transactionDate DESC")
    List<Transaction> findByEmployeeIdAndYearAndMonth(
            @Param("employeeId") Long employeeId,
            @Param("year") int year,
            @Param("month") int month);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.amount < 0 AND t.transactionDate BETWEEN :start AND :end")
    BigDecimal sumDebitsBetweenDates(@Param("start") LocalDate start,
                                     @Param("end") LocalDate end);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.amount > 0 AND t.transactionDate BETWEEN :start AND :end")
    BigDecimal sumCreditsBetweenDates(@Param("start") LocalDate start,
                                      @Param("end") LocalDate end);
}
