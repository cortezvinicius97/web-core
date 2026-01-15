package com.example.service;

import com.example.model.Employee;
import com.example.repository.EmployeeRepository;
import com.example.exception.EmployeeNotFoundException;
import com.vcinsidedigital.webcore.annotations.Service;
import com.vcinsidedigital.webcore.annotations.Inject;
import java.util.List;

@Service
public class EmployeeService {

    @Inject
    private EmployeeRepository repository;

    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public Employee createEmployee(Employee employee) {
        employee.setId(null); // Garante que será um novo ID
        return repository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee newEmployee) {
        return repository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());
                    return repository.save(employee);
                })
                .orElseGet(() -> {
                    newEmployee.setId(id);
                    return repository.save(newEmployee);
                });
    }

    public void deleteEmployee(Long id) {
        if (!repository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }
        repository.deleteById(id);
    }
}