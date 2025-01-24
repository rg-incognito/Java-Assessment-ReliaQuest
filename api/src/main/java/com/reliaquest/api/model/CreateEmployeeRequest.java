package com.reliaquest.api.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateEmployeeRequest {

    @NotBlank(message = "Employee name cannot be blank")
    private String name;

    @Positive(message = "Employee salary must be greater than zero") private Integer salary;

    @Min(value = 16, message = "Employee age must be at least 16")
    @Max(value = 75, message = "Employee age cannot exceed 75")
    private Integer age;

    @NotBlank(message = "Employee title cannot be blank")
    private String title;

    public CreateEmployeeRequest(final String name, final Integer salary, final Integer age, final String title) {
        this.name = name;
        this.salary = salary;
        this.age = age;
        this.title = title;
    }
}
