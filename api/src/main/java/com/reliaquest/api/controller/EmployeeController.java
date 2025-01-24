package com.reliaquest.api.controller;

import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeRequest> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(final EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {

        EmployeeResponse response = employeeService.getAllEmployees();
        if (response != null && response.getData() != null) {
            return ResponseEntity.ok(response.getData());
        } else {
            logger.warn("Received null or empty response from employee service.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        List<Employee> response = employeeService.getEmployeesByNameSearch(searchString);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {

        UUID uuid = UUID.fromString(id);
        Employee employee = employeeService.getEmployeeById(uuid);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        if (highestSalary != null) {
            return ResponseEntity.ok(highestSalary);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {

        List<String> topTenNames = employeeService.getTopTenHighestEarningEmployeeNames();
        if (topTenNames != null) {
            return ResponseEntity.ok(topTenNames);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Employee> createEmployee(CreateEmployeeRequest employeeInput) {

        Employee createdEmployee = employeeService.createEmployee(employeeInput);

        if (createdEmployee != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);

        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Or appropriate error code
        }
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {

        boolean deleted = employeeService.deleteEmployee(id);
        if (deleted) {
            return ResponseEntity.ok(id); // Return the deleted employee's ID

        } else {
            return ResponseEntity.notFound().build(); // Employee not found
        }
    }
}
