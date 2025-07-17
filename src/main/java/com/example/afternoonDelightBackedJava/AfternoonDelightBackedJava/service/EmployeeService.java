package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeeDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeePinDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.MealParticipation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public interface EmployeeService {
    Optional<Employee> findByEmployeePin(String pin);

    EmployeeDTO getByUsername(String username);

    EmployeeDTO getByPin(String pin);

    void uploadProfilePicture(String username, MultipartFile file) throws IOException;

    void uploadProfilePictureByPin(String pin, MultipartFile file) throws IOException;

    byte[] getProfilePicture(String username);

    String getImageType(String username);

    List<EmployeeDTO> getAllEmployees();

    EmployeeDTO createEmployee(EmployeeDTO EmployeeDTO);
    
    List<EmployeePinDTO> getAllEmployeePins(String searchParams);
    
    
}
