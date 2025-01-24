package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.reliaquest.api.Exception.ExternalApiException;
import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.CreateEmployeeResponse;
import com.reliaquest.api.model.DeleteResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeResponse;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

class EmployeeClientTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @InjectMocks
    private EmployeeClient employeeClient;

    @Mock
    private RestTemplate restTemplate;

    private final String baseUrl = "http://mock-api.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        employeeClient = new EmployeeClient(baseUrl, restTemplateBuilder);
    }

    @Test
    void testGetAllEmployees_Success() {
        Employee employee1 = new Employee(UUID.randomUUID(), "John Doe", 1000, 30, "Software Engineer", "john@xyz.com");
        Employee employee2 = new Employee(UUID.randomUUID(), "Jane Doe", 2000, 25, "Product Manager", "jane@xyz.com");
        EmployeeResponse mockResponse = new EmployeeResponse(Arrays.asList(employee1, employee2), "success");

        when(restTemplate.getForEntity(eq(baseUrl + "/employee"), eq(EmployeeResponse.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        EmployeeResponse result = employeeClient.getAllEmployees();

        assertNotNull(result);
        assertEquals(2, result.getData().size());
        assertEquals("John Doe", result.getData().get(0).getEmployee_name());
        verify(restTemplate, times(1)).getForEntity(any(String.class), eq(EmployeeResponse.class));
    }

    @Test
    void testGetAllEmployees_Exception() {
        when(restTemplate.getForEntity(eq(baseUrl + "/employee"), eq(EmployeeResponse.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(ExternalApiException.class, () -> employeeClient.getAllEmployees());
        verify(restTemplate, times(1)).getForEntity(any(String.class), eq(EmployeeResponse.class));
    }

    @Test
    void testGetEmployeeById_Success() {
        UUID id = UUID.randomUUID();
        Employee mockEmployee = new Employee(id, "John Doe", 1000, 30, "Software Engineer", "john@xyz.com");
        CreateEmployeeResponse mockResponse = new CreateEmployeeResponse(mockEmployee, "success");

        when(restTemplate.getForEntity(
                        eq(baseUrl + "/employee/{id}"), eq(CreateEmployeeResponse.class), eq(id.toString())))
                .thenReturn(ResponseEntity.ok(mockResponse));

        Employee result = employeeClient.getEmployeeById(id);

        assertNotNull(result);
        assertEquals("John Doe", result.getEmployee_name());
        verify(restTemplate, times(1))
                .getForEntity(any(String.class), eq(CreateEmployeeResponse.class), any(String.class));
    }

    @Test
    void testGetEmployeeById_NotFound() {
        UUID id = UUID.randomUUID();

        when(restTemplate.getForEntity(
                        eq(baseUrl + "/employee/{id}"), eq(CreateEmployeeResponse.class), eq(id.toString())))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(ExternalApiException.class, () -> employeeClient.getEmployeeById(id));
        verify(restTemplate, times(1))
                .getForEntity(any(String.class), eq(CreateEmployeeResponse.class), any(String.class));
    }

    @Test
    void testGetEmployeeById_Exception() {
        UUID id = UUID.randomUUID();

        when(restTemplate.getForEntity(
                        eq(baseUrl + "/employee/{id}"), eq(CreateEmployeeResponse.class), eq(id.toString())))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(ExternalApiException.class, () -> employeeClient.getEmployeeById(id));
        verify(restTemplate, times(1))
                .getForEntity(any(String.class), eq(CreateEmployeeResponse.class), any(String.class));
    }

    @Test
    void testCreateEmployee_Success() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("John Doe", 1000, 30, "Software Engineer");
        Employee mockEmployee =
                new Employee(UUID.randomUUID(), "John Doe", 1000, 30, "Software Engineer", "john@xyz.com");
        CreateEmployeeResponse mockResponse = new CreateEmployeeResponse(mockEmployee, "success");

        when(restTemplate.exchange(
                        eq(baseUrl + "/employee"), eq(HttpMethod.POST), any(), eq(CreateEmployeeResponse.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        Employee result = employeeClient.createEmployee(request);

        assertNotNull(result);
        assertEquals("John Doe", result.getEmployee_name());
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.POST), any(), eq(CreateEmployeeResponse.class));
    }

    @Test
    void testCreateEmployee_Exception() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("John Doe", 1000, 30, "Software Engineer");

        when(restTemplate.exchange(
                        eq(baseUrl + "/employee"), eq(HttpMethod.POST), any(), eq(CreateEmployeeResponse.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(ExternalApiException.class, () -> employeeClient.createEmployee(request));
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.POST), any(), eq(CreateEmployeeResponse.class));
    }

    @Test
    void testDeleteEmployee_Success() {
        String employeeName = "John Doe";
        DeleteResponse mockResponse = new DeleteResponse(true, "success");

        when(restTemplate.exchange(eq(baseUrl + "/employee"), eq(HttpMethod.DELETE), any(), eq(DeleteResponse.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        boolean result = employeeClient.deleteEmployee(employeeName);

        assertTrue(result);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.DELETE), any(), eq(DeleteResponse.class));
    }

    @Test
    void testDeleteEmployee_Exception() {
        String employeeName = "John Doe";

        when(restTemplate.exchange(eq(baseUrl + "/employee"), eq(HttpMethod.DELETE), any(), eq(DeleteResponse.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(ExternalApiException.class, () -> employeeClient.deleteEmployee(employeeName));
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.DELETE), any(), eq(DeleteResponse.class));
    }
}
