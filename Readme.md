# Web Framework

A lightweight, Spring Boot-inspired web framework for Java that provides automatic component scanning, dependency injection, RESTful API development, and a powerful plugin system.

## 🚀 Installation

### Gradle (Groovy)

```groovy
implementation 'com.vcinsidedigital:web-core:1.0.7'
```

### Gradle (Kotlin)

```kotlin
implementation("com.vcinsidedigital:web-core:1.0.7")
```

### Maven

```xml
<dependency>
    <groupId>com.vcinsidedigital</groupId>
    <artifactId>web-core</artifactId>
    <version>1.0.7</version>
</dependency>
```

## ✨ Features

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
- 🔌 **Plugin System**: Extensible architecture with plugin support
- 🎨 **Custom Annotations**: Plugins can create custom component and parameter annotations
- 🌐 **Server Customization**: Plugins can customize port, host, request/response handling
- 🚪 **Gateway System**: Add server capabilities like sessions, WebSockets, metrics via Gateways

## 📋 Requirements

- Java 11 or higher
- Gson 2.8.9+ (for JSON serialization)

## 🎯 Quick Start

### 1. Create Your Main Application Class

```java
package com.example;

import com.vcinsidedigital.webcore.WebServerApplication;
import com.vcinsidedigital.webcore.annotations.WebApplication;

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

import com.vcinsidedigital.webcore.annotations.Repository;
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

import com.vcinsidedigital.webcore.annotations.Service;
import com.vcinsidedigital.webcore.annotations.Inject;
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

### 5. Create a Controller

```java
package com.example.controller;

import com.vcinsidedigital.webcore.annotations.*;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.example.service.EmployeeService;
import com.example.model.Employee;
import java.util.List;

@RestController
public class EmployeeController {
    
    @Inject
    private EmployeeService employeeService;
    
    @Get("/employees")
    public HttpResponse getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return new HttpResponse()
            .status(200)
            .contentType("application/json")
            .body(employees);
    }
    
    @Get("/employees/{id}")
    public HttpResponse getEmployee(@Path("id") Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return new HttpResponse()
            .status(200)
            .contentType("application/json")
            .body(employee);
    }
    
    @Post("/employees")
    public HttpResponse createEmployee(@Body Employee employee) {
        Employee created = employeeService.createEmployee(employee);
        return new HttpResponse()
            .status(201)
            .contentType("application/json")
            .body(created);
    }
}
```

### 6. Run Your Application

```bash
# Default port (8080) and host (localhost)
java com.example.Application

# Custom port
java com.example.Application --port=9090

# Custom host
java com.example.Application --host=0.0.0.0

# Custom port and host
java com.example.Application --port=9090 --host=0.0.0.0
```

## 📚 Annotations Reference

### Component Annotations

| Annotation | Description | Usage | Default |
|------------|-------------|-------|---------|
| `@WebApplication` | Marks the main application class | Main class only | - |
| `@RestController` | Marks a REST controller (returns JSON) | Controller classes | `path = "/api"` |
| `@Controller` | Marks an HTML controller | Controller classes | `path = ""` |
| `@Service` | Marks a service component | Service classes | - |
| `@Repository` | Marks a repository component | Repository classes | - |
| `@Component` | Generic component annotation | Any managed class | - |
| `@Plugin` | Marks a plugin class | Plugin classes | - |

### Dependency Injection

| Annotation | Description | Usage |
|------------|-------------|-------|
| `@Inject` | Injects dependencies | Fields, constructors |

### HTTP Method Mappings

| Annotation | HTTP Method | Description |
|------------|-------------|-------------|
| `@Get` | GET | Retrieve resources |
| `@Post` | POST | Create resources |
| `@Put` | PUT | Update/replace resources |
| `@Patch` | PATCH | Partial update resources |
| `@Delete` | DELETE | Delete resources |
| `@Options` | OPTIONS | CORS preflight requests |

### Parameter Annotations

| Annotation | Description | Example |
|------------|-------------|---------|
| `@Path` | Extract path variables | `@Path("id") Long id` |
| `@Body` | Parse request body | `@Body Employee employee` |
| `@Query` | Extract query parameters | `@Query("name") String name` |

### Other Annotations

| Annotation | Description | Example |
|------------|-------------|---------|
| `@Middleware` | Apply middleware to controller/method | `@Middleware({AuthMiddleware.class})` |
| `@ResponseStatus` | Set custom HTTP status code | `@ResponseStatus(HttpStatus.CREATED)` |

## 🔌 Plugin System

The framework includes a powerful plugin system that allows you to extend functionality, customize server initialization, and add cross-cutting concerns.

### Creating a Plugin

#### Basic Plugin

```java
package com.example.plugin;

import com.vcinsidedigital.webcore.annotations.Plugin;
import com.vcinsidedigital.webcore.plugin.AbstractPlugin;
import com.vcinsidedigital.webcore.WebServerApplication;

@Plugin
public class MyPlugin extends AbstractPlugin {
    
    @Override
    public String getId() {
        return "com.example.myplugin"; // Unique plugin ID
    }
    
    @Override
    public String getName() {
        return "My Plugin";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public void onLoad(WebServerApplication application) {
        System.out.println("Plugin loaded!");
    }
    
    @Override
    public void onStart(WebServerApplication application) {
        System.out.println("Plugin started!");
    }
}
```

#### Plugin with Custom Server Initialization

```java
package com.example.plugin;

import com.vcinsidedigital.webcore.annotations.Plugin;
import com.vcinsidedigital.webcore.plugin.AbstractPlugin;
import com.vcinsidedigital.webcore.routing.Router;
import com.vcinsidedigital.webcore.http.HttpRequest;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.vcinsidedigital.webcore.WebServerApplication;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

@Plugin
public class CustomServerPlugin extends AbstractPlugin {
    
    @Override
    public String getId() {
        return "com.example.customserver";
    }
    
    @Override
    public boolean isInitializeServer() {
        return true; // This plugin will initialize the server
    }
    
    @Override
    public void onServerInit(Router router, String[] args, String hostname, int port) {
        try {
            // Create custom HTTP server
            HttpServer server = HttpServer.create(new InetSocketAddress(hostname, port), 0);
            
            // Configure thread pool
            server.setExecutor(Executors.newFixedThreadPool(20));
            
            // Add main context
            server.createContext("/", exchange -> {
                try {
                    HttpRequest request = parseRequest(exchange);
                    HttpResponse response = router.handleRequest(request);
                    
                    // Add CORS headers
                    response.header("Access-Control-Allow-Origin", "*")
                            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS")
                            .header("Access-Control-Allow-Headers", "Content-Type, Authorization");
                    
                    WebServerApplication.sendResponse(exchange, response);
                } catch (Exception e) {
                    e.printStackTrace();
                    WebServerApplication.sendErrorResponse(exchange, 500, "Internal Server Error");
                }
            });
            
            // Add health check endpoint
            server.createContext("/health", exchange -> {
                String response = "{\"status\": \"UP\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.getResponseBody().close();
            });
            
            server.start();
            
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║   ✅ Application started successfully!             ║");
            System.out.println("║   🌐 Server running at: http://" + hostname + ":" + port + "      ║");
            System.out.println("║   🔧 Custom server by: " + getName() + "           ║");
            System.out.println("║   📝 Press Ctrl+C to stop                         ║");
            System.out.println("╚════════════════════════════════════════════════════╝\n");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to start custom server", e);
        }
    }
    
    private HttpRequest parseRequest(HttpExchange exchange) throws Exception {
        // Parse request implementation
        // See PluginUtils or WebServerApplication for reference
    }
}
```

### Plugin Components

Plugins can include their own components (controllers, services, repositories):

```
plugin-project/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── example/
│                   └── plugin/
│                       ├── MyPlugin.java
│                       ├── controller/
│                       │   └── PluginController.java
│                       ├── service/
│                       │   └── PluginService.java
│                       └── middleware/
│                           └── PluginMiddleware.java
```

**Plugin Controller Example:**

```java
package com.example.plugin.controller;

import com.vcinsidedigital.webcore.annotations.*;
import com.vcinsidedigital.webcore.http.HttpResponse;

@RestController(path = "/plugin")
public class PluginController {
    
    @Get("/info")
    public HttpResponse getInfo() {
        return new HttpResponse()
            .status(200)
            .body("{\"plugin\": \"MyPlugin\", \"version\": \"1.0.0\"}");
    }
}
```

### Registering Plugins

#### Manual Registration

```java
package com.example;

import com.vcinsidedigital.webcore.WebServerApplication;
import com.vcinsidedigital.webcore.annotations.WebApplication;
import com.example.plugin.MyPlugin;

@WebApplication
public class Application extends WebServerApplication {
    
    public static void main(String[] args) {
        // Register plugins before running
        registerPlugin(new MyPlugin());
        
        WebServerApplication.run(Application.class, args);
    }
}
```

#### Automatic Registration

Plugins with `@Plugin` annotation are automatically discovered and registered:

```java
package com.example.plugin;

import com.vcinsidedigital.webcore.annotations.Plugin;
import com.vcinsidedigital.webcore.plugin.AbstractPlugin;

@Plugin // Automatically registered during package scan
public class MyPlugin extends AbstractPlugin {
    // Plugin implementation
}
```

### Plugin Lifecycle

1. **Registration** - Plugins are registered (manually or via `@Plugin`)
2. **Loading** - `onLoad()` is called for each plugin
3. **Component Scanning** - Plugin packages are scanned for components
4. **Starting** - `onStart()` is called for each plugin
5. **Server Initialization** - `onServerInit()` is called if `isInitializeServer()` returns true

### Plugin Interface Methods

| Method | Description | When Called |
|--------|-------------|-------------|
| `getId()` | Returns unique plugin identifier | On registration |
| `getName()` | Returns plugin display name | Throughout lifecycle |
| `getVersion()` | Returns plugin version | Throughout lifecycle |
| `isInitializeServer()` | Whether plugin handles server init | Before server start |
| `onLoad(application)` | Plugin initialization | After registration |
| `onStart(application)` | Post-initialization setup | After all components loaded |
| `onServerInit(router, args, hostname, port)` | Custom server initialization | Only if `isInitializeServer()` is true |
| `getBasePackage()` | Package to scan for components | During component scanning |

### Plugin Use Cases

#### 1. CORS Plugin

```java
@Plugin
public class CorsPlugin extends AbstractPlugin {
    
    @Override
    public String getId() {
        return "com.framework.cors";
    }
    
    @Override
    public boolean isInitializeServer() {
        return true;
    }
    
    @Override
    public void onServerInit(Router router, String[] args, String hostname, int port) {
        // Add CORS headers to all responses
        // Implementation similar to CustomServerPlugin example
    }
}
```

#### 2. Logging Plugin

```java
@Plugin
public class LoggingPlugin extends AbstractPlugin {
    
    @Override
    public String getId() {
        return "com.framework.logging";
    }
    
    @Override
    public void onStart(WebServerApplication application) {
        System.out.println("Logging plugin activated - all requests will be logged");
    }
}
```

#### 3. Authentication Plugin

```java
@Plugin
public class AuthPlugin extends AbstractPlugin {
    
    @Override
    public String getId() {
        return "com.framework.auth";
    }
    
    @Override
    public void onLoad(WebServerApplication application) {
        // Register auth middleware globally
        // Setup JWT validation
    }
}
```

## 🎨 Extensible Annotation System

Plugins can create custom annotations for components and parameters, extending the framework without modifying core code.

### Creating Custom Component Annotations

```java
// 1. Define the annotation
package com.myplugin.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApiController {
    String version() default "v1";
}

// 2. Create a handler
package com.myplugin.handlers;

import com.vcinsidedigital.webcore.extensibility.ComponentAnnotationHandler;

public class ApiControllerHandler implements ComponentAnnotationHandler {
    
    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ApiController.class;
    }
    
    @Override
    public boolean isComponent(Class<?> clazz) {
        return clazz.isAnnotationPresent(ApiController.class);
    }
    
    @Override
    public boolean isController(Class<?> clazz) {
        return true;
    }
    
    @Override
    public String getBasePath(Class<?> clazz) {
        ApiController annotation = clazz.getAnnotation(ApiController.class);
        return "/" + annotation.version();
    }
}

// 3. Register in plugin
@Plugin
public class CustomAnnotationsPlugin extends AbstractPlugin {
    
    @Override
    public void onLoad(WebServerApplication application) {
        AnnotationHandlerRegistry.getInstance()
            .registerComponentHandler(new ApiControllerHandler());
    }
}

// 4. Use it!
@ApiController(version = "v2")
public class UserController {
    @Get("/users")  // Full path: /v2/users
    public HttpResponse getUsers() { ... }
}
```

### Creating Custom Parameter Annotations

```java
// 1. Define the annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Header {
    String value();
    boolean required() default true;
}

// 2. Create a handler
public class HeaderParameterHandler implements ParameterAnnotationHandler {
    
    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return Header.class;
    }
    
    @Override
    public boolean canHandle(Parameter parameter) {
        return parameter.isAnnotationPresent(Header.class);
    }
    
    @Override
    public Object resolveParameter(Parameter parameter, ParameterContext context) {
        Header header = parameter.getAnnotation(Header.class);
        String value = context.getHeader(header.value());
        
        if (value == null && header.required()) {
            throw new IllegalArgumentException("Required header '" + header.value() + "' is missing");
        }
        
        return value;
    }
}

// 3. Register in plugin
@Override
public void onLoad(WebServerApplication application) {
    AnnotationHandlerRegistry.getInstance()
        .registerParameterHandler(new HeaderParameterHandler());
}

// 4. Use it!
@Get("/secure/data")
public HttpResponse getData(
    @Header("Authorization") String token,
    @Header(value = "X-API-Key", required = false) String apiKey
) {
    // Headers automatically extracted and validated
}
```

### Example Custom Annotations

- **`@Cookie`** - Extract cookies from requests
- **`@Session`** - Access session data
- **`@CurrentUser`** - Inject authenticated user
- **`@Valid`** - Validate request body
- **`@RateLimit`** - Apply rate limiting
- **`@Cached`** - Cache responses

## 🛠️ Server Customization

Plugins can customize server configuration without taking full control of initialization.

### Server Configuration

```java
@Plugin
public class ServerCustomizationPlugin extends AbstractPlugin {
    
    @Override
    public ServerConfiguration getServerConfiguration() {
        return new ServerConfiguration() {
            
            @Override
            public Integer getPort() {
                return 9000; // Custom port
            }
            
            @Override
            public String getHost() {
                return "0.0.0.0"; // Bind to all interfaces
            }
            
            @Override
            public HttpRequest customizeRequest(HttpExchange exchange) throws Exception {
                // Custom request processing
                System.out.println("Processing: " + exchange.getRequestURI());
                return null; // Return null to use default parsing
            }
            
            @Override
            public void customizeResponse(HttpResponse response, HttpExchange exchange) {
                // Add custom headers to ALL responses
                response.header("X-Powered-By", "MyFramework");
                response.header("X-Frame-Options", "DENY");
            }
        };
    }
}
```

### Conflict Detection

Only ONE plugin can customize each aspect:

```
🔌 Loading plugins:
  ✅ Port customization registered by: ServerPlugin1
  ├─ ❌ Port is already customized by another plugin. Only one plugin can customize the port.
```

**Customizable Aspects:**
- ✅ Port (one plugin)
- ✅ Host (one plugin)
- ✅ Request parsing (one plugin)
- ✅ Response handling (one plugin)
- ✅ Gateways (multiple plugins allowed)

## 🚪 Gateway System

Gateways add server capabilities like sessions, metrics, WebSockets, etc.

### Creating a Gateway

```java
package com.myplugin.gateway;

import com.vcinsidedigital.webcore.server.Gateway;
import com.sun.net.httpserver.HttpServer;

public class SessionGateway implements Gateway {
    
    private final Map<String, Map<String, Object>> sessions = new ConcurrentHashMap<>();
    
    @Override
    public String getName() {
        return "Session Gateway";
    }
    
    @Override
    public void initialize(HttpServer server) throws Exception {
        // Add session endpoint
        server.createContext("/session", exchange -> {
            String sessionId = getOrCreateSession(exchange);
            Map<String, Object> sessionData = sessions.get(sessionId);
            
            String response = "{\"sessionId\": \"" + sessionId + "\"}";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        });
    }
    
    @Override
    public void onStart() {
        System.out.println("Session management active");
    }
    
    @Override
    public void onStop() {
        sessions.clear();
    }
    
    private String getOrCreateSession(HttpExchange exchange) {
        // Session logic
    }
}
```

### Registering Gateways

```java
@Plugin
public class SessionPlugin extends AbstractPlugin {
    
    @Override
    public void onLoad(WebServerApplication application) {
        ServerCustomizer.getInstance().registerGateway(new SessionGateway());
        ServerCustomizer.getInstance().registerGateway(new MetricsGateway());
    }
}
```

### Gateway Examples

#### Metrics Gateway

```java
public class MetricsGateway implements Gateway {
    
    private final AtomicLong requestCount = new AtomicLong(0);
    private final long startTime = System.currentTimeMillis();
    
    @Override
    public void initialize(HttpServer server) throws Exception {
        server.createContext("/metrics", exchange -> {
            long uptime = (System.currentTimeMillis() - startTime) / 1000;
            long requests = requestCount.incrementAndGet();
            
            String metrics = String.format(
                "{\"uptime\": %d, \"requests\": %d}",
                uptime, requests
            );
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, metrics.length());
            exchange.getResponseBody().write(metrics.getBytes());
            exchange.getResponseBody().close();
        });
    }
}
```

#### WebSocket Gateway (Example)

```java
public class WebSocketGateway implements Gateway {
    
    @Override
    public String getName() {
        return "WebSocket Gateway";
    }
    
    @Override
    public void initialize(HttpServer server) throws Exception {
        server.createContext("/ws", exchange -> {
            // WebSocket handshake and handling
            // Upgrade connection to WebSocket protocol
        });
    }
}
```

### Multiple Gateways

Unlike server configuration, **multiple gateways can be registered**:

```
🔌 Initializing gateways:
  ├─ Session Gateway initialized
  ├─ Metrics Gateway initialized
  ├─ WebSocket Gateway initialized
  ├─ Health Check Gateway initialized
    └─ Session management active
    └─ Metrics endpoint available at /metrics
    └─ WebSocket server ready
    └─ Health check at /health
```

### Plugin ID Conflicts

Each plugin must have a unique ID. If two plugins share the same ID, a `DuplicatePluginException` is thrown and the conflicting plugin will not be registered:

```
📦 Scanning package: com.example
  Found 11 components:
  ✅ Plugin registered: CustomServerPlugin v1.0.0 (ID: com.example.plugins.hello)
    ├─ ❌ Plugin ID conflict detected!
  Plugin ID: 'com.example.plugins.hello'
  Already registered: CustomServerPlugin v1.0.0
  Attempted to register: Hello Plugin v1.0.0
  Each plugin must have a unique ID.
    ├─ RestController: SecureController
    ├─ ⏭️  Skipped (failed plugin): HelloPluginController  # Components from failed plugin are skipped
```

**Important:** When a plugin fails to register due to ID conflict, all its components (controllers, services, etc.) are automatically skipped and will not be registered in the application.

**Best Practice:** Use reverse domain notation for plugin IDs:
- ✅ `com.company.project.pluginname`
- ✅ `org.team.feature.plugin`
- ❌ `myplugin`
- ❌ `plugin1`

## 📖 Examples

### Path Variables

```java
@Get("/users/{id}/posts/{postId}")
public HttpResponse getPost(@Path("id") Long userId, @Path("postId") Long postId) {
    Post post = postService.findPost(userId, postId);
    return new HttpResponse()
        .status(200)
        .body(post);
}
```

### Query Parameters

```java
@Get("/search")
public HttpResponse search(@Query("role") String role, @Query("department") String dept) {
    List<Employee> results = employeeService.search(role, dept);
    return new HttpResponse()
        .status(200)
        .body(results);
}

// Usage: GET /api/search?role=Developer&department=IT
```

### Request Body

```java
@Post("/employees")
public HttpResponse createEmployee(@Body Employee employee) {
    Employee created = employeeService.save(employee);
    return new HttpResponse()
        .status(201)
        .body(created);
}
```

### Using Middleware

```java
@RestController
@Middleware({LoggingMiddleware.class, AuthMiddleware.class})
public class SecureController {
    
    @Get("/secure/data")
    public HttpResponse getSecureData() {
        return new HttpResponse()
            .status(200)
            .body("{\"data\": \"sensitive\"}");
    }
}
```

### Custom HTTP Status

```java
@RestController
public class StatusController {
    
    @ResponseStatus(HttpStatus.CREATED)
    @Post("/items")
    public HttpResponse createItem(@Body String item) {
        return new HttpResponse()
            .body("{\"message\": \"Created\"}");
    }
    
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Delete("/items/{id}")
    public HttpResponse deleteItem(@Path("id") Long id) {
        itemService.delete(id);
        return new HttpResponse();
    }
}
```

### HTML Pages

```java
@Controller
public class PageController {
    
    @Get("/")
    public HttpResponse home() {
        String html = "<h1>Welcome Home</h1><p>This is the home page</p>";
        return new HttpResponse()
            .status(200)
            .contentType("text/html; charset=utf-8")
            .body(html);
    }
}
```

## ⚙️ Configuration

### Base Package Scanning

```java
@WebApplication(basePackage = "com.myproject")
public class Application extends WebServerApplication {
    public static void main(String[] args) {
        WebServerApplication.run(Application.class, args);
    }
}
```

### Custom Port and Host

```bash
# Command line
java -jar myapp.jar --port=8585 --host=0.0.0.0

# Defaults
# port: 8080
# host: localhost
```

### Controller Base Paths

```java
// Default: All routes start with /api
@RestController
public class EmployeeController {
    @Get("/employees")  // Full path: /api/employees
    public HttpResponse getAll() { ... }
}

// Custom base path
@RestController(path = "/v2")
public class V2Controller {
    @Get("/data")  // Full path: /v2/data
    public HttpResponse getData() { ... }
}

// No prefix
@RestController(path = "")
public class RootController {
    @Get("/health")  // Full path: /health
    public HttpResponse health() { ... }
}
```

## 🧪 Testing Your API

### Using cURL

```bash
# Get all employees
curl http://localhost:8080/api/employees

# Get specific employee
curl http://localhost:8080/api/employees/1

# Create employee
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

## 📁 Project Structure

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
│               ├── model/
│               │   └── Employee.java
│               ├── middleware/
│               │   └── AuthMiddleware.java
│               └── plugin/
│                   ├── MyPlugin.java
│                   └── controller/
│                       └── PluginController.java
```

## 🔄 Application Lifecycle

When you run `WebServerApplication.run()`, the framework:

1. **Scans** the base package for annotated classes
2. **Registers** plugins (manual and auto-discovered)
3. **Loads** plugins (`onLoad()`)
4. **Scans** plugin packages for components
5. **Registers** all components in DI container
6. **Injects** dependencies
7. **Maps** controller methods to HTTP routes
8. **Starts** plugins (`onStart()`)
9. **Initializes** server (plugin or default)
10. **Listens** for incoming requests

### Console Output

```
╔════════════════════════════════════════════════════╗
║         WEB FRAMEWORK - Starting Application       ║
╚════════════════════════════════════════════════════╝

📦 Scanning package: com.example
  Found 4 components:
    ├─ Repository: EmployeeRepository
    ├─ Service: EmployeeService
    ├─ RestController: EmployeeController
  ✅ Plugin registered: My Plugin v1.0.0 (ID: com.example.myplugin)

🔌 Loading plugins:
  ├─ Loaded: My Plugin

📦 Scanning plugin packages:
  Scanning: com.example.plugin
  Found 1 components:
    ├─ RestController: PluginController

🔌 Registering routes:
  [ROUTE] GET /api/employees -> EmployeeController.getAllEmployees()
  [ROUTE] GET /api/employees/{id} -> EmployeeController.getEmployee()
  [ROUTE] POST /api/employees -> EmployeeController.createEmployee()
  [ROUTE] GET /plugin/info -> PluginController.getInfo()

🚀 Starting plugins:
  ├─ Started: My Plugin

🚀 Starting HTTP server...

╔════════════════════════════════════════════════════╗
║   ✅ Application started successfully!             ║
║   🌐 Server running at: http://localhost:8080      ║
║   📝 Press Ctrl+C to stop                          ║
╚════════════════════════════════════════════════════╝
```

## 🆚 Comparison with Spring Boot

| Feature | This Framework | Spring Boot |
|---------|---------------|-------------|
| Dependency Injection | ✅ | ✅ |
| Auto-configuration | ✅ | ✅ |
| REST Controllers | ✅ | ✅ |
| Path Variables | ✅ | ✅ |
| Query Parameters | ✅ | ✅ |
| Request Body | ✅ | ✅ |
| Middleware System | ✅ | ✅ |
| Plugin System | ✅ | ❌ |
| Embedded Server | ✅ (Native) | ✅ (Tomcat) |
| JPA/Hibernate | ❌ | ✅ |
| Security | ❌ (Plugin) | ✅ |
| Validation | ❌ | ✅ |

## ⚠️ Limitations

- No built-in database integration (implement custom repositories)
- No built-in validation framework
- No built-in security (can be added via plugins)
- No aspect-oriented programming (AOP)
- No transaction management
- Basic request handling (can be extended via plugins)

## 🚀 Future Enhancements

- ✨ Exception handlers (`@ExceptionHandler`)
- ✨ WebSocket support
- ✨ Async request handling
- ✨ Bean validation integration
- ✨ OpenAPI/Swagger documentation
- ✨ Built-in metrics and monitoring
- ✨ Database integration plugins
- ✨ Security plugins (JWT, OAuth2)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit issues and pull requests.

## 📄 License

This project is open source and available under the Apache 2.0 License.

## 💬 Support

For questions and support, please open an issue on GitHub.

---

**Happy Coding! 🚀**