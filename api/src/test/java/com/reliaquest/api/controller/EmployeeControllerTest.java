package com.reliaquest.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.service.EmployeeService;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(employeeService);
    }

    @Test
    void testGetAllEmployees() throws Exception {
        // Mocking the service response
        Employee employee1 = new Employee(UUID.randomUUID(), "John Doe", 1000, 30, "Software Engineer", "abc@xyz.com");
        Employee employee2 = new Employee(UUID.randomUUID(), "Jane Doe", 2000, 25, "Product Manager", "abc@xyz.com");
        EmployeeResponse response = new EmployeeResponse(Arrays.asList(employee1, employee2), "success");

        when(employeeService.getAllEmployees()).thenReturn(response);

        // Perform the GET request and verify the response
        mockMvc.perform(get("/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].employee_name").value("John Doe"))
                .andExpect(jsonPath("$[1].employee_name").value("Jane Doe"));
    }

    @Test
    void testGetEmployeesByNameSearch() throws Exception {
        // Mocking the service response
        String searchString = "John";
        Employee employee = new Employee(UUID.randomUUID(), "John Doe", 1000, 30, "Software Engineer", "abc@xyz.com");
        when(employeeService.getEmployeesByNameSearch(searchString)).thenReturn(List.of(employee));

        // Perform the GET request and verify the response
        mockMvc.perform(get("/search/{searchString}", searchString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].employee_name").value("John Doe"));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        // Mocking the service response
        UUID employeeId = UUID.randomUUID();
        Employee employee = new Employee(employeeId, "John Doe", 1000, 30, "Software Engineer", "abc@xyz.com");
        when(employeeService.getEmployeeById(employeeId)).thenReturn(employee);

        // Perform the GET request and verify the response
        mockMvc.perform(get("/{id}", employeeId.toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeId.toString()))
                .andExpect(jsonPath("$.employee_name").value("John Doe"));
    }

    @Test
    void testGetHighestSalaryOfEmployees() throws Exception {
        // Mocking the service response
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(2000);

        // Perform the GET request and verify the response
        mockMvc.perform(get("/highestSalary").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("2000"));
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() throws Exception {
        // Mocking the service response
        List<String> topTenNames = List.of("John Doe", "Jane Doe");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(topTenNames);

        // Perform the GET request and verify the response
        mockMvc.perform(get("/topTenHighestEarningEmployeeNames").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").value("John Doe"))
                .andExpect(jsonPath("$[1]").value("Jane Doe"));
    }

    @Test
    void testCreateEmployee() throws Exception {
        // Mocking the service response
        CreateEmployeeRequest request = new CreateEmployeeRequest("John Doe", 1000, 30, "Software Engineer");
        Employee createdEmployee =
                new Employee(UUID.randomUUID(), "John Doe", 1000, 30, "Software Engineer", "abc@xyz.com");

        when(employeeService.createEmployee(any(CreateEmployeeRequest.class))).thenReturn(createdEmployee);

        // Perform the POST request and verify the response
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employee_name").value("John Doe"));
    }

    @Test
    void testDeleteEmployeeById() throws Exception {
        // Mocking the service response
        String employeeId = UUID.randomUUID().toString();
        when(employeeService.deleteEmployee(employeeId)).thenReturn(true);

        mockMvc.perform(delete("/{id}", employeeId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(employeeId));
    }

    @Test
    void testDeleteEmployeeByIdNotFound() throws Exception {
        String invalidEmployeeId = UUID.randomUUID().toString();

        // Mocking the service to throw an exception for invalid ID
        when(employeeService.deleteEmployee(invalidEmployeeId)).thenThrow(new RuntimeException("Employee not found"));

        // Perform the DELETE request and verify the error response
        mockMvc.perform(delete("/{id}", invalidEmployeeId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testCreateEmployeeInvalidRequest() throws Exception {
        // Mocking an invalid request with missing fields
        CreateEmployeeRequest invalidRequest = new CreateEmployeeRequest(null, 0, 0, null);

        // Perform the POST request and verify the error response
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is5xxServerError());
    }
}
