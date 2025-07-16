package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.emums.RequestStatus;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.MealRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealRequestRepository extends JpaRepository<MealRequest, Long> {
    List<MealRequest> findByStatus(RequestStatus status);
    List<MealRequest> findByEmployeeIdAndStatus(Long employeeId, RequestStatus status);
    List<MealRequest> findByRequestedDateBetween(LocalDate startDate, LocalDate endDate);
    List<MealRequest> findByEmployeeIdAndRequestedDateBetween(
            Long employeeId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT mr FROM MealRequest mr WHERE mr.status = :status ORDER BY mr.createdAt DESC")
    List<MealRequest> findByStatusOrderByCreatedAtDesc(@Param("status") RequestStatus status);

    @Query("SELECT mr FROM MealRequest mr WHERE mr.employee.id = :employeeId ORDER BY mr.createdAt DESC")
    List<MealRequest> findByEmployeeIdOrderByCreatedAtDesc(@Param("employeeId") Long employeeId);
}
