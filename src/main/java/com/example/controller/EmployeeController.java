package com.example.controller;

import com.example.model.Employee;
import com.example.service.EmployeeService;
import com.vcinsidedigital.webcore.annotations.*;
import java.util.List;

@RestController
public class EmployeeController {

    @Inject
    private EmployeeService employeeService;

    @Get("/employees")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @Get("/employees/{id}")
    public Employee getEmployee(@Path("id") Long id) {
        return employeeService.getEmployeeById(id);
    }

    @Post("/employees")
    public Employee createEmployee(@Body Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @Put("/employees/{id}")
    public Employee updateEmployee(@Path("id") Long id, @Body Employee employee) {
        return employeeService.updateEmployee(id, employee);
    }

    @Patch("/employees/{id}")
    public Employee patchEmployee(@Path("id") Long id, @Body Employee employee) {
        Employee existing = employeeService.getEmployeeById(id);

        if (employee.getName() != null) {
            existing.setName(employee.getName());
        }
        if (employee.getRole() != null) {
            existing.setRole(employee.getRole());
        }

        return employeeService.updateEmployee(id, existing);
    }

    @Delete("/employees/{id}")
    public void deleteEmployee(@Path("id") Long id) {
        employeeService.deleteEmployee(id);
    }
}
