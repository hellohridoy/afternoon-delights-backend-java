package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.components;

import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.service.EmployeeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BalanceScheduler {

    private final EmployeeService employeeService;

    public BalanceScheduler(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void checkBalancesAndSendWarnings() {
        employeeService.getAllEmployeePins(null);
    }
}
