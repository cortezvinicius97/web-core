# Web Framework

A lightweight, Spring Boot-inspired web framework for Java that provides automatic component scanning, dependency injection, and RESTful API development capabilities.

## 🚀 Usage

Gradle Groovy

```Groovy
implementation 'com.vcinsidedigital:web-core:1.0.0'
```

Gradle Kotlin
```kotlin
implementation('com.vcinsidedigital:web-core:1.0.0')
```

Maven
```xml
<dependencies>
    <dependency>
        <groupId>com.vcinsidedigital</groupId>
        <artifactId>web-core</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```



## Features

- 🚀 **Auto-Configuration**: Automatic component scanning and registration
- 💉 **Dependency Injection**: Field and constructor injection support
- 🌐 **Built-in Web Server**: Uses Java's native HttpServer (no external dependencies)
- 🎯 **RESTful APIs**: Simple annotations for creating REST endpoints
- 📦 **Component Management**: Automatic lifecycle management for components
- 🔄 **Path Variables**: Extract parameters from URLs
- 📝 **Request Body Parsing**: Automatic JSON deserialization
- ❓ **Query Parameters**: Easy access to URL query strings
- 🎨 **Default API Prefix**: `@RestController` automatically uses `/api` base path
- 🔒 **Middleware System**: Built-in support for request interceptors
- 📊 **HTTP Status Codes**: Custom status codes with `@ResponseStatus`

## Requirements

- Java 11 or higher
- Gson 2.8.9+ (for JSON serialization)

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.9</version>
</dependency>
```

## Quick Start

### 1. Create Your Main Application Class

```java
package com.example;

import com.framework.WebServerApplication;
import com.framework.annotations.WebApplication;

@WebApplication
public class Application extends WebServerApplication {
    
    public static void main(String[] args) {
        WebServerApplication.run(Application.class, args);
    }
}
```

### 2. Create a Model

```java
package com.example.model;

public class Employee {
    private Long id;
    private String name;
    private String role;
    
    // Constructors, getters and setters
}
```

### 3. Create a Repository

```java
package com.example.repository;

import com.framework.annotations.Repository;
import com.example.model.Employee;
import java.util.*;

@Repository
public class EmployeeRepository {
    private final Map<Long, Employee> database = new HashMap<>();
    
    public List<Employee> findAll() {
        return new ArrayList<>(database.values());
    }
    
    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }
    
    public Employee save(Employee employee) {
        database.put(employee.getId(), employee);
        return employee;
    }
    
    public void deleteById(Long id) {
        database.remove(id);
    }
}
```

### 4. Create a Service

```java
package com.example.service;

import com.framework.annotations.Service;
import com.framework.annotations.Inject;
import com.example.repository.EmployeeRepository;
import com.example.model.Employee;
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
            .orElseThrow(() -> new RuntimeException("Employee not found"));
    }
    
    public Employee createEmployee(Employee employee) {
        return repository.save(employee);
    }
    
    public void deleteEmployee(Long id) {
        repository.deleteById(id);
    }
}
```

### 5. Create a REST Controller

```java
package com.example.controller;

import com.framework.annotations.*;
import com.example.service.EmployeeService;
import com.example.model.Employee;
import java.util.List;

// Default path is /api
@RestController
public class EmployeeController {
    
    @Inject
    private EmployeeService employeeService;
    
    @Get("/employees")  // Full path: /api/employees
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
    
    @Get("/employees/{id}")  // Full path: /api/employees/{id}
    public Employee getEmployee(@Path("id") Long id) {
        return employeeService.getEmployeeById(id);
    }
    
    @Post("/employees")  // Full path: /api/employees
    public Employee createEmployee(@Body Employee employee) {
        return employeeService.createEmployee(employee);
    }
    
    @Put("/employees/{id}")  // Full path: /api/employees/{id}
    public Employee updateEmployee(@Path("id") Long id, @Body Employee employee) {
        employee.setId(id);
        return employeeService.createEmployee(employee);
    }
    
    @Delete("/employees/{id}")  // Full path: /api/employees/{id}
    public void deleteEmployee(@Path("id") Long id) {
        employeeService.deleteEmployee(id);
    }
}

// Custom base path
@RestController(path = "/apicustom")
public class CustomController {
    
    @Get("/data")  // Full path: /apicustom/data
    public String getData() {
        return "{\"data\": \"custom\"}";
    }
}
```

### 6. Run Your Application

```bash
java com.example.Application
```

Or specify a custom port:

```bash
java com.example.Application --port=9090
```

## Annotations Reference

### Component Annotations

| Annotation | Description | Usage | Default |
|------------|-------------|-------|---------|
| `@WebApplication` | Marks the main application class | Main class only | - |
| `@RestController` | Marks a REST controller (returns JSON) | Controller classes | `path = "/api"` |
| `@Controller` | Marks an HTML controller | Controller classes | `path = ""` |
| `@Service` | Marks a service component | Service classes | - |
| `@Repository` | Marks a repository component | Repository classes | - |
| `@Component` | Generic component annotation | Any managed class | - |

### Dependency Injection

| Annotation | Description | Usage |
|------------|-------------|-------|
| `@Inject` | Injects dependencies | Fields, constructors, parameters |

### HTTP Method Mappings

| Annotation | HTTP Method | Description |
|------------|-------------|-------------|
| `@Get` | GET | Retrieve resources |
| `@Post` | POST | Create resources |
| `@Put` | PUT | Update/replace resources |
| `@Patch` | PATCH | Partial update resources |
| `@Delete` | DELETE | Delete resources |

### Parameter Annotations

| Annotation | Description | Example |
|------------|-------------|---------|
| `@Path` | Extract path variables | `@Path("id") Long id` |
| `@Body` | Parse request body | `@Body Employee employee` |
| `@Query` | Extract query parameters | `@Query("name") String name` |

## Examples

### Path Variables

```java
@Get("/users/{id}/posts/{postId}")
public Post getPost(@Path("id") Long userId, @Path("postId") Long postId) {
    return postService.findPost(userId, postId);
}
```

### Query Parameters

```java
@Get("/search")
public List<Employee> search(@Query("role") String role, @Query("department") String dept) {
    return employeeService.search(role, dept);
}

// Usage: GET /search?role=Developer&department=IT
```

### Request Body

```java
@Post("/employees")
public Employee createEmployee(@Body Employee employee) {
    return employeeService.save(employee);
}

// Request body:
// {
//   "name": "John Doe",
//   "role": "Developer"
// }
```

### Multiple Parameters

```java
@Put("/employees/{id}")
public Employee updateEmployee(
    @Path("id") Long id,
    @Body Employee employee,
    @Query("notify") Boolean notify
) {
    employee.setId(id);
    Employee updated = employeeService.update(employee);
    if (notify) {
        notificationService.send(updated);
    }
    return updated;
}
```

## Configuration

### Base Package Scanning

By default, the framework scans the package where your main application class is located. You can specify a custom base package:

```java
@WebApplication(basePackage = "com.myproject")
public class Application extends WebServerApplication {
    public static void main(String[] args) {
        WebServerApplication.run(Application.class, args);
    }
}
```

### Custom Port

```java
// Via command line
java -jar myapp.jar --port=8585

// Default port is 8080
```

## Application Lifecycle

When you run `WebServerApplication.run()`, the framework:

1. **Scans** the base package for annotated classes
2. **Registers** all components in the DI container
3. **Injects** dependencies into registered components
4. **Maps** controller methods to HTTP routes
5. **Starts** the embedded HTTP server
6. **Listens** for incoming requests

### Console Output

```
╔════════════════════════════════════════════════════╗
║         WEB FRAMEWORK - Starting Application      ║
╚════════════════════════════════════════════════════╝

📦 Scanning package: com.example
  Found 4 components:
    ├─ Repository: EmployeeRepository
    ├─ Service: EmployeeService
    ├─ RestController: EmployeeController
    ├─ RestController: ProductController

🔌 Registering routes:
  [ROUTE] GET /api/employees -> EmployeeController.getAllEmployees()
  [ROUTE] GET /api/employees/{id} -> EmployeeController.getEmployee()
  [ROUTE] POST /api/employees -> EmployeeController.createEmployee()
  [ROUTE] PUT /api/employees/{id} -> EmployeeController.updateEmployee()
  [ROUTE] PATCH /api/employees/{id} -> EmployeeController.patchEmployee()
  [ROUTE] DELETE /api/employees/{id} -> EmployeeController.deleteEmployee()

🚀 Starting HTTP server...

╔════════════════════════════════════════════════════╗
║   ✅ Application started successfully!             ║
║   🌐 Server running at: http://localhost:8080      ║
║   📝 Press Ctrl+C to stop                         ║
╚════════════════════════════════════════════════════╝
```

## Testing Your API

### Using cURL

```bash
# Get all employees (default /api prefix)
curl http://localhost:8080/api/employees

# Get specific employee
curl http://localhost:8080/api/employees/1

# Create new employee
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","role":"Designer"}'

# Update employee
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"John Smith","role":"Senior Developer"}'

# Delete employee
curl -X DELETE http://localhost:8080/api/employees/1
```

### Using PowerShell

```powershell
# Get all employees
Invoke-WebRequest http://localhost:8080/api/employees

# Get with headers
curl http://localhost:8080/api/employees `
  -Headers @{ Authorization = "Bearer token123" }

# Post with body
curl -Method POST http://localhost:8080/api/employees `
  -Headers @{ "Content-Type" = "application/json" } `
  -Body '{"name":"Jane Doe","role":"Designer"}'
```

### Using Postman or Insomnia

Import these endpoints:
- GET `http://localhost:8080/api/employees` (default /api prefix)
- GET `http://localhost:8080/api/employees/{id}`
- POST `http://localhost:8080/api/employees`
- PUT `http://localhost:8080/api/employees/{id}`
- PATCH `http://localhost:8080/api/employees/{id}`
- DELETE `http://localhost:8080/api/employees/{id}`

**Note:** All `@RestController` endpoints automatically have the `/api` prefix unless you specify a custom path.

## Advanced Features

### Constructor Injection

```java
@Service
public class EmployeeService {
    private final EmployeeRepository repository;
    
    @Inject
    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }
}
```

### Field Injection

```java
@Service
public class EmployeeService {
    @Inject
    private EmployeeRepository repository;
}
```

### Controller Base Path

**`@RestController` has a default base path of `/api`:**

```java
// Default: All routes start with /api
@RestController
public class EmployeeController {
    
    @Get("/employees")  // Full path: /api/employees
    public List<Employee> getAll() {
        return employeeService.findAll();
    }
}

// Custom base path
@RestController(path = "/apicustom")
public class CustomController {
    
    @Get("/data")  // Full path: /apicustom/data
    public String getData() {
        return "{\"data\": \"custom\"}";
    }
}

// Root level (no prefix)
@RestController(path = "")
public class RootController {
    
    @Get("/health")  // Full path: /health
    public String health() {
        return "{\"status\": \"ok\"}";
    }
}
```

**`@Controller` has no default base path:**

```java
@Controller  // Default path is empty ""
public class PageController {
    
    @Get("/")  // Full path: /
    public String home() {
        return "<h1>Home Page</h1>";
    }
    
    @Get("/about")  // Full path: /about
    public String about() {
        return "<h1>About Page</h1>";
    }
}
```

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── example/
│               ├── Application.java
│               ├── controller/
│               │   └── EmployeeController.java
│               ├── service/
│               │   └── EmployeeService.java
│               ├── repository/
│               │   └── EmployeeRepository.java
│               └── model/
│                   └── Employee.java
```

## Comparison with Spring Boot

| Feature | This Framework | Spring Boot |
|---------|---------------|-------------|
| Dependency Injection | ✅ | ✅ |
| Auto-configuration | ✅ | ✅ |
| REST Controllers | ✅ | ✅ |
| Path Variables | ✅ | ✅ |
| Query Parameters | ✅ | ✅ |
| Request Body | ✅ | ✅ |
| Embedded Server | ✅ (Native) | ✅ (Tomcat/Jetty) |
| JPA/Hibernate | ❌ | ✅ |
| Security | ❌ | ✅ |
| Validation | ❌ | ✅ |

## Limitations

- No database integration (implement your own repositories)
- No built-in validation framework
- No security/authentication features
- No aspect-oriented programming (AOP)
- No transaction management
- Single-threaded request handling (can be extended)

## Future Enhancements

Potential features for future versions:
- ✨ Exception handlers (`@ExceptionHandler`)
- ✨ Request/Response interceptors
- ✨ CORS support (`@CrossOrigin`)
- ✨ File upload handling
- ✨ WebSocket support
- ✨ Async request handling
- ✨ Bean validation integration
- ✨ OpenAPI/Swagger documentation
- ✨ Health check endpoints
- ✨ Metrics and monitoring

## Contributing

Contributions are welcome! Feel free to submit issues and pull requests.

## License

This project is open source and available under the MIT License.

## Support

For questions and support, please open an issue on GitHub.

---

**Happy Coding! 🚀**