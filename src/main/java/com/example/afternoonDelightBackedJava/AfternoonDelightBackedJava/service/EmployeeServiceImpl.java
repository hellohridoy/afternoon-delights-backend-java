package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeePersonalInfoDto;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.mapper.EmployeeMapper;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    
    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeePersonalInfoDto getByUsername(String username) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return EmployeeMapper.toDto(employee);
    }

    @Override
    public EmployeePersonalInfoDto getByPin(String pin) {
        Employee employee = employeeRepository.findByPin(pin)
                .orElseThrow(() -> new RuntimeException("Invalid PIN"));
        return EmployeeMapper.toDto(employee);
    }

    @Override
    public void uploadProfilePicture(String username, MultipartFile file) throws IOException {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        employee.setProfilePicture(file.getBytes());
        employee.setImageType(file.getContentType());
        employeeRepository.save(employee);
    }

    @Override
    public void uploadProfilePictureByPin(String pin, MultipartFile file) throws IOException {
        Optional<Employee> employee = Optional.ofNullable(employeeRepository.findByPin(pin).orElseThrow(
                () -> new RuntimeException("Employee Not faound with this pin")));
       try {
           employee.get().setProfilePicture(file.getBytes());
           employee.get().setImageType(file.getContentType());
           employeeRepository.save(employee.get());
           
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    @Override
    public byte[] getProfilePicture(String username) {
        return employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getProfilePicture();
    }

    @Override
    public String getImageType(String username) {
        return employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getImageType();
    }

    @Override
    public List<EmployeePersonalInfoDto> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeePersonalInfoDto createEmployee(EmployeePersonalInfoDto EmployeePersonalInfoDto) {
        Employee employee = EmployeeMapper.toEntity(EmployeePersonalInfoDto);
        employee.setRole(com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.emums.Role.valueOf(EmployeePersonalInfoDto.getRole()));
        return EmployeeMapper.toDto(employeeRepository.save(employee));
    }
}