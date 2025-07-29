package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeePinDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.emums.Role;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByPin(String pin);
    Optional<Employee> findByUsername(String username);
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByEmployeeId(String employeeId);

    List<Employee> findAllByIsActiveTrue();

    // Fixed query to match EmployeePinDTO constructor
    @Query("SELECT new com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeePinDTO(e.id, e.pin, e.email, e.balance) " +
            "FROM Employee e " +
            "WHERE e.isActive = true " +
            "AND (:search IS NULL OR :search = '' OR " +
            "LOWER(e.pin) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.username) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<EmployeePinDTO> getAllEmployeePins(@Param("search") String search);

    // Alternative method for simple PIN lookup
    @Query("SELECT new com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeePinDTO(e.pin, e.username, e.designation, e.email) " +
            "FROM Employee e " +
            "WHERE e.isActive = true " +
            "ORDER BY e.username")
    List<EmployeePinDTO> getAllEmployeePinsSimple();

    // Existence check methods (needed for saveEmployees method)
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPin(String pin);
    boolean existsByEmployeeId(String employeeId);

    // Count active employees
    Long countByIsActiveTrue();

    // Role and designation queries
    List<Employee> findByRoleAndIsActiveTrue(Role role);
    List<Employee> findByDesignationContainingIgnoreCaseAndIsActiveTrue(String designation);

    // Search methods for different scenarios
    List<Employee> findByUsernameContainingIgnoreCaseOrPinContainingOrEmailContainingIgnoreCase(
            String username, String pin, String email);

    List<Employee> findByIsActiveTrueOrderByUsername();

    @Query("SELECT e FROM Employee e WHERE e.isActive = true AND " +
            "(LOWER(e.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.pin) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Employee> searchActiveEmployees(@Param("search") String search);

    // Balance-related queries
    List<Employee> findByBalanceLessThan(BigDecimal threshold);
    List<Employee> findByBalanceLessThanEqual(BigDecimal maxBalance);

    List<Employee> findByBalanceGreaterThan(BigDecimal threshold);
    List<Employee> findByBalanceGreaterThanEqual(BigDecimal minBalance);

    List<Employee> findByBalanceBetween(BigDecimal minBalance, BigDecimal maxBalance);

    List<Employee> findByBalance(BigDecimal exactBalance);

    @Query("SELECT SUM(e.balance) FROM Employee e WHERE e.isActive = true")
    BigDecimal sumAllBalances();

    @Query("SELECT SUM(e.balance) FROM Employee e")
    BigDecimal sumAllBalancesIncludingInactive();

    // Additional useful balance queries
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.balance < 0 AND e.isActive = true")
    Long countEmployeesWithNegativeBalance();

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.balance = 0 AND e.isActive = true")
    Long countEmployeesWithZeroBalance();

    @Query("SELECT AVG(e.balance) FROM Employee e WHERE e.isActive = true")
    BigDecimal getAverageBalance();

    @Query("SELECT MIN(e.balance) FROM Employee e WHERE e.isActive = true")
    BigDecimal getMinimumBalance();

    @Query("SELECT MAX(e.balance) FROM Employee e WHERE e.isActive = true")
    BigDecimal getMaximumBalance();

    // Find employees with specific balance conditions
    @Query("SELECT e FROM Employee e WHERE e.balance < :threshold AND e.isActive = true ORDER BY e.balance ASC")
    List<Employee> findEmployeesWithLowBalance(@Param("threshold") BigDecimal threshold);

    @Query("SELECT e FROM Employee e WHERE e.balance > :threshold AND e.isActive = true ORDER BY e.balance DESC")
    List<Employee> findEmployeesWithHighBalance(@Param("threshold") BigDecimal threshold);

    // Balance range with active status filter
    @Query("SELECT e FROM Employee e WHERE e.balance BETWEEN :minBalance AND :maxBalance AND e.isActive = true ORDER BY e.balance DESC")
    List<Employee> findActiveEmployeesByBalanceRange(@Param("minBalance") BigDecimal minBalance,
                                                     @Param("maxBalance") BigDecimal maxBalance);

    // Batch operations
    @Query("SELECT e FROM Employee e WHERE e.username IN :usernames OR e.email IN :emails OR e.pin IN :pins")
    List<Employee> findByUsernamesOrEmailsOrPins(@Param("usernames") List<String> usernames,
                                                 @Param("emails") List<String> emails,
                                                 @Param("pins") List<String> pins);
}