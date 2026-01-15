package com.example.controller;

import com.example.middleware.CorsMiddleware;
import com.example.middleware.LoggingMiddleware;
import com.example.model.Employee;
import com.example.service.EmployeeService;
import com.vcinsidedigital.webcore.annotations.*;
import com.vcinsidedigital.webcore.http.HttpStatus;

import java.util.List;

@RestController(path = "/public")
@Middleware({CorsMiddleware.class, LoggingMiddleware.class})
public class PublicApiController {

    @Inject
    private EmployeeService employeeService;

    // GET /api/public/employees - Accessible from any origin
    @Get("/employees")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // GET /api/public/employees/{id} - Accessible from any origin
    @Get("/employees/{id}")
    public Employee getEmployee(@Path("id") Long id) {
        return employeeService.getEmployeeById(id);
    }

    // POST /api/public/employees - Accessible from any origin
    @ResponseStatus(HttpStatus.CREATED)
    @Post("/employees")
    public Employee createEmployee(@Body Employee employee) {
        return employeeService.createEmployee(employee);
    }

    // Health check endpoint
    @Get("/health")
    public String healthCheck() {
        return "{\"status\": \"ok\", \"timestamp\": " + System.currentTimeMillis() + "}";
    }
}

