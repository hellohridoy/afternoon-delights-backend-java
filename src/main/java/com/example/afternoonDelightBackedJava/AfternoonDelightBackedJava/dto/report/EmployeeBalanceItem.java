package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.report;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeBalanceItem {
    private String pin;
    private String name;
    private BigDecimal balance;

    // Conversion helper method
    public static EmployeeBalanceItem fromEmployee(Employee employee) {
        EmployeeBalanceItem item = new EmployeeBalanceItem();
        item.setPin(employee.getPin());
        item.setName(employee.getUsername());
        item.setBalance(employee.getBalance());
        return item;
    }
}