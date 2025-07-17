package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeePinDTO;
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
    Optional<Employee> findByUsername(String username);

    Optional<Employee> findByPin(String pin);

    @Query("SELECT new com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeePinDTO(e.id, e.pin, e.email, e.balance) " +
            "FROM Employee e " +
            "WHERE (:search IS NULL OR LOWER(e.pin) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<EmployeePinDTO> getAllEmployeePins(@Param("search") String search);

    @Query("SELECT SUM(e.balance) FROM Employee e")
    BigDecimal sumAllBalances();

    List<Employee> findByBalanceLessThanEqual(BigDecimal max);
    List<Employee> findByBalanceGreaterThanEqual(BigDecimal min);
    List<Employee> findByBalanceBetween(BigDecimal min, BigDecimal max);
}
