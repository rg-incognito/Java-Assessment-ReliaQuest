package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeResponse;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class EmployeeServiceTest {

    @Mock
    private EmployeeClient employeeClient;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllEmployees_Success() {
        // Arrange
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(Arrays.asList(
                new Employee(UUID.randomUUID(), "John Doe", 50000, 30, "Software Engineer", "john.doe@example.com"),
                new Employee(UUID.randomUUID(), "Jane Smith", 60000, 28, "Project Manager", "jane.smith@example.com")));
        when(employeeClient.getAllEmployees()).thenReturn(mockResponse);

        // Act
        EmployeeResponse response = employeeService.getAllEmployees();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getData().size());
        assertEquals("John Doe", response.getData().get(0).getEmployee_name());
        assertEquals("jane.smith@example.com", response.getData().get(1).getEmployee_email());
        verify(employeeClient, times(1)).getAllEmployees();
    }

    @Test
    void testGetEmployeesByNameSearch_Success() {
        // Arrange
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(Arrays.asList(
                new Employee(UUID.randomUUID(), "John Doe", 50000, 30, "Software Engineer", "john.doe@example.com"),
                new Employee(UUID.randomUUID(), "Jane Doe", 60000, 28, "Project Manager", "jane.doe@example.com"),
                new Employee(
                        UUID.randomUUID(), "Alice Johnson", 40000, 25, "QA Engineer", "alice.johnson@example.com")));
        when(employeeClient.getAllEmployees()).thenReturn(mockResponse);

        // Act
        List<Employee> result = employeeService.getEmployeesByNameSearch("Doe");

        // Assert
        assertEquals(2, result.size());
        assertTrue(
                result.stream().allMatch(employee -> employee.getEmployee_name().contains("Doe")));
        verify(employeeClient, times(1)).getAllEmployees();
    }

    @Test
    void testGetEmployeeById_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        Employee mockEmployee = new Employee(id, "John Doe", 50000, 30, "Software Engineer", "john.doe@example.com");
        when(employeeClient.getEmployeeById(id)).thenReturn(mockEmployee);

        // Act
        Employee result = employeeService.getEmployeeById(id);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getEmployee_name());
        assertEquals(50000, result.getEmployee_salary());
        assertEquals("john.doe@example.com", result.getEmployee_email());
        verify(employeeClient, times(1)).getEmployeeById(id);
    }

    @Test
    void testGetHighestSalaryOfEmployees_Success() {
        // Arrange
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(Arrays.asList(
                new Employee(UUID.randomUUID(), "John Doe", 50000, 30, "Software Engineer", "john.doe@example.com"),
                new Employee(UUID.randomUUID(), "Jane Smith", 60000, 28, "Project Manager", "jane.smith@example.com"),
                new Employee(
                        UUID.randomUUID(), "Alice Johnson", 40000, 25, "QA Engineer", "alice.johnson@example.com")));
        when(employeeClient.getAllEmployees()).thenReturn(mockResponse);

        // Act
        int highestSalary = employeeService.getHighestSalaryOfEmployees();

        // Assert
        assertEquals(60000, highestSalary);
        verify(employeeClient, times(1)).getAllEmployees();
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_Success() {
        // Arrange
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(Arrays.asList(
                new Employee(UUID.randomUUID(), "John Doe", 50000, 30, "Software Engineer", "john.doe@example.com"),
                new Employee(UUID.randomUUID(), "Jane Smith", 60000, 28, "Project Manager", "jane.smith@example.com"),
                new Employee(
                        UUID.randomUUID(), "Alice Johnson", 40000, 25, "QA Engineer", "alice.johnson@example.com")));
        when(employeeClient.getAllEmployees()).thenReturn(mockResponse);

        // Act
        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

        // Assert
        assertEquals(3, result.size());
        assertEquals("Jane Smith", result.get(0)); // Highest salary
        assertEquals("John Doe", result.get(1)); // Second highest salary
        verify(employeeClient, times(1)).getAllEmployees();
    }

    @Test
    void testCreateEmployee_Success() {
        // Arrange
        CreateEmployeeRequest request = new CreateEmployeeRequest("John Doe", 50000, 30, "Software Engineer");
        Employee mockEmployee =
                new Employee(UUID.randomUUID(), "John Doe", 50000, 30, "Software Engineer", "john.doe@example.com");
        when(employeeClient.createEmployee(request)).thenReturn(mockEmployee);

        // Act
        Employee result = employeeService.createEmployee(request);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getEmployee_name());
        assertEquals("Software Engineer", result.getEmployee_title());
        verify(employeeClient, times(1)).createEmployee(request);
    }

    @Test
    void testDeleteEmployee_Success() {
        UUID id = UUID.randomUUID();
        Employee mockEmployee = new Employee(id, "John Doe", 50000, 30, "Software Engineer", "john.doe@example.com");
        when(employeeClient.getEmployeeById(id)).thenReturn(mockEmployee);
        when(employeeClient.deleteEmployee("John Doe")).thenReturn(true);

        boolean result = employeeService.deleteEmployee(id.toString());

        assertTrue(result);
        verify(employeeClient, times(1)).getEmployeeById(id);
        verify(employeeClient, times(1)).deleteEmployee("John Doe");
    }
}
