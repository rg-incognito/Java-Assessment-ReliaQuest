package com.reliaquest.api.service;

import com.reliaquest.api.Exception.EmployeeNotFoundException;
import com.reliaquest.api.Exception.ExternalApiException;
import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.CreateEmployeeResponse;
import com.reliaquest.api.model.DeleteMockEmployeeInput;
import com.reliaquest.api.model.DeleteResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeResponse;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EmployeeClient {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public EmployeeClient(@Value("${employee.api.url}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplateBuilder.build();
    }

    public EmployeeResponse getAllEmployees() {
        String url = baseUrl + "/employee";
        try {
            ResponseEntity<EmployeeResponse> response = restTemplate.getForEntity(url, EmployeeResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                logger.error("Failed to fetch all employees. Status code: {}", response.getStatusCode());
                throw new ExternalApiException("Failed to fetch all employees.");
            }
        } catch (Exception e) {
            logger.error("Error fetching all employees", e);
            throw new ExternalApiException("Error occurred while fetching employees.");
        }
    }

    public Employee getEmployeeById(UUID id) {
        String url = baseUrl + "/employee/{id}";
        try {
            ResponseEntity<CreateEmployeeResponse> response =
                    restTemplate.getForEntity(url, CreateEmployeeResponse.class, id.toString());
            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            } else if (response.getStatusCode().is4xxClientError()) {
                logger.warn("Employee with id {} not found", id);
                throw new EmployeeNotFoundException(id);
            } else {
                logger.error("Failed to retrieve employee with id {}. Status code: {}", id, response.getStatusCode());
                throw new ExternalApiException("Failed to retrieve employee. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error fetching employee by id: {}", id, e);
            throw new ExternalApiException("Error occurred while fetching employee by ID: " + id);
        }
    }

    public Employee createEmployee(CreateEmployeeRequest employeeInput) {
        String url = baseUrl + "/employee";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CreateEmployeeRequest> requestEntity = new HttpEntity<>(employeeInput, headers);

            ResponseEntity<CreateEmployeeResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, requestEntity, CreateEmployeeResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getData();
            } else {
                logger.error("Failed to create employee. Status code: {}", response.getStatusCode());
                throw new ExternalApiException("Failed to create employee. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error creating employee", e);
            throw new ExternalApiException("Error occurred while creating employee.");
        }
    }

    public boolean deleteEmployee(String name) {
        String url = baseUrl + "/employee"; // Correct DELETE URL
        try {
            DeleteMockEmployeeInput deleteEmployeeRequest = new DeleteMockEmployeeInput();
            deleteEmployeeRequest.setName(name);
            HttpEntity<DeleteMockEmployeeInput> requestEntity = new HttpEntity<>(deleteEmployeeRequest);

            ResponseEntity<DeleteResponse> response =
                    restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, DeleteResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return (boolean) response.getBody().getData();
            } else {
                logger.error("Failed to delete employee {}. Status code: {}", name, response.getStatusCode());
                throw new ExternalApiException(
                        "Failed to delete employee " + name + ". Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error deleting employee {}", name, e);
            throw new ExternalApiException("Error occurred while deleting employee: " + name);
        }
    }
}
