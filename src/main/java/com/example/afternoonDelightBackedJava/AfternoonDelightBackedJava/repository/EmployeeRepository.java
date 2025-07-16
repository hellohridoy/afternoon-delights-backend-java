package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUsername(String username);
    Optional<Employee> findByPin(String pin);
    List<Employee> findByIsActiveTrue();
    boolean existsByUsername(String username);
    boolean existsByPin(String pin);
    boolean existsByEmail(String email);
}
