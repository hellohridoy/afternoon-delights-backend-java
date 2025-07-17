package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeeDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.EmployeePinDTO;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.mapper.EmployeeMapper;
import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;

    @Override
    public Optional<Employee> findByEmployeePin(String pin) {
        return employeeRepository.findByPin(pin);
    }

    @Override
    public EmployeeDTO getByUsername(String username) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return EmployeeMapper.toDto(employee);
    }

    @Override
    public EmployeeDTO getByPin(String pin) {
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
                () -> new RuntimeException("Employee Not found with this pin")));
        try {
            employee.get().setProfilePicture(file.getBytes());
            employee.get().setImageType(file.getContentType());
            employeeRepository.save(employee.get());

        } catch (Exception e) {
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
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO EmployeeDTO) {
        Employee employee = EmployeeMapper.toEntity(EmployeeDTO);
        employee.setRole(com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.emums.Role.valueOf(EmployeeDTO.getRole()));
        return EmployeeMapper.toDto(employeeRepository.save(employee));
    }

    @Override
    public List<EmployeePinDTO> getAllEmployeePins(String searchParams) {
        List<Employee> employees = employeeRepository.findAll(); // or use searchParams if needed

        for (Employee employee : employees) {
            if (employee.getBalance() != null) {
                BigDecimal balance = employee.getBalance();

                if (balance.compareTo(BigDecimal.ZERO) < 0) {
                    emailService.sendEmail(
                            employee.getEmail(),
                            "⚠ Negative Balance Alert",
                            "Dear " + employee.getUsername() + ",\n\nYour balance is negative. Please take necessary action.\n\nRegards,\nSupport Team"
                    );
                } else if (balance.compareTo(new BigDecimal("500")) < 0) {
                    emailService.sendEmail(
                            employee.getEmail(),
                            "⚠ Low Balance Warning",
                            "Dear " + employee.getUsername() + ",\n\nYour balance is below 500. Please recharge soon.\n\nRegards,\nSupport Team"
                    );
                }
            }
        }

        return employeeRepository.getAllEmployeePins(searchParams);
    }
}