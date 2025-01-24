package com.reliaquest.api.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Employee {
    private UUID id;

    @NotBlank(message = "Employee name cannot be blank")
    private String employee_name;

    @Positive(message = "Employee salary must be greater than zero") private Integer employee_salary;

    @Min(value = 16, message = "Employee age must be at least 16")
    @Max(value = 75, message = "Employee age cannot exceed 75")
    private Integer employee_age;

    @NotBlank(message = "Employee title cannot be blank")
    private String employee_title;

    private String employee_email;
}
