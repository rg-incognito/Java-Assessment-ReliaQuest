package com.reliaquest.api.service;

import com.reliaquest.api.Exception.EmployeeNotFoundException;
import com.reliaquest.api.Exception.ExternalApiException;
import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeResponse;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
    private final EmployeeClient employeeClient;

    @Autowired
    public EmployeeService(final EmployeeClient employeeClient) {
        this.employeeClient = employeeClient;
    }

    public EmployeeResponse getAllEmployees() {
        EmployeeResponse response = employeeClient.getAllEmployees();
        if (response == null || response.getData() == null) {
            throw new ExternalApiException("Failed to fetch employee data.");
        }
        return response;
    }

    public List<Employee> getEmployeesByNameSearch(final String searchString) {
        EmployeeResponse response = employeeClient.getAllEmployees(); // Fetch all, then filter
        if (response != null && response.getData() != null) {
            return response.getData().stream()
                    .filter(employee ->
                            employee.getEmployee_name().toLowerCase().contains(searchString.toLowerCase()))
                    .collect(Collectors.toList());
        }
        throw new ExternalApiException("Failed to fetch employee data for name search.");
    }

    public Employee getEmployeeById(final UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Employee ID cannot be null.");
        }
        try {
            return employeeClient.getEmployeeById(uuid);
        } catch (EmployeeNotFoundException e) {
            throw e; // Let the controller advice handle this.
        } catch (ExternalApiException e) {
            throw new RuntimeException("Failed to fetch employee by ID.", e);
        }
    }

    public Integer getHighestSalaryOfEmployees() {
        EmployeeResponse response = employeeClient.getAllEmployees();
        return response.getData().stream()
                .map(Employee::getEmployee_salary)
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new ExternalApiException("No employees found to calculate the highest salary."));
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        EmployeeResponse response = employeeClient.getAllEmployees();
        if (response != null && response.getData() != null) {
            return response.getData().stream()
                    .sorted(Comparator.comparingInt(Employee::getEmployee_salary)
                            .reversed())
                    .limit(10)
                    .map(Employee::getEmployee_name)
                    .collect(Collectors.toList());
        }
        return null;
    }

    public Employee createEmployee(final CreateEmployeeRequest employeeInput) {
        return employeeClient.createEmployee(employeeInput);
    }

    public boolean deleteEmployee(final String id) {
        try {
            Employee employee = employeeClient.getEmployeeById(UUID.fromString(id));
            return employeeClient.deleteEmployee(employee.getEmployee_name());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete employee with ID: " + id, e);
        }
    }
}
