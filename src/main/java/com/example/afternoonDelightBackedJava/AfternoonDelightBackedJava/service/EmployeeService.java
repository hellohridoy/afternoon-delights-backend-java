package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeePersonalInfoDto;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface EmployeeService {
    EmployeePersonalInfoDto getByUsername(String username);
    EmployeePersonalInfoDto getByPin(String pin);
    void uploadProfilePicture(String username, MultipartFile file) throws IOException;
    void uploadProfilePictureByPin(String pin, MultipartFile file) throws IOException;
    byte[] getProfilePicture(String username);
    String getImageType(String username);
    List<EmployeePersonalInfoDto> getAllEmployees();
    EmployeePersonalInfoDto createEmployee(EmployeePersonalInfoDto EmployeePersonalInfoDto);
}
