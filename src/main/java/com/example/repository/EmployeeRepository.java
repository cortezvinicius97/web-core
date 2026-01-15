package com.example.repository;

import com.example.model.Employee;
import com.vcinsidedigital.webcore.annotations.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class EmployeeRepository {
    private final Map<Long, Employee> database = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public EmployeeRepository() {
        // Dados iniciais
        save(new Employee(null, "João Silva", "Developer"));
        save(new Employee(null, "Maria Santos", "Manager"));
        save(new Employee(null, "Pedro Costa", "Designer"));
    }

    public List<Employee> findAll() {
        return new ArrayList<>(database.values());
    }

    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    public Employee save(Employee employee) {
        if (employee.getId() == null) {
            employee.setId(idGenerator.getAndIncrement());
        }
        database.put(employee.getId(), employee);
        return employee;
    }

    public void deleteById(Long id) {
        database.remove(id);
    }

    public boolean existsById(Long id) {
        return database.containsKey(id);
    }
}

