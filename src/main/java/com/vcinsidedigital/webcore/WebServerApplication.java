package com.vcinsidedigital.webcore;


import com.vcinsidedigital.webcore.annotations.*;
import com.vcinsidedigital.webcore.core.*;
import com.vcinsidedigital.webcore.routing.Router;
import com.vcinsidedigital.webcore.http.*;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class WebServerApplication {

    private static DIContainer container;
    private static Router router;
    private static HttpServer server;
    private static int port = 8080;

    public static void run(Class<?> applicationClass, String[] args) {
        try {
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║         WEB FRAMEWORK - Starting Application      ║");
            System.out.println("╚════════════════════════════════════════════════════╝\n");

            // Parse port from args
            port = parsePort(args);

            // Initialize container and router
            container = new DIContainer();
            router = new Router();

            // Get base package
            String basePackage = getBasePackage(applicationClass);

            // Scan and register components
            System.out.println("📦 Scanning package: " + basePackage);
            scanAndRegister(basePackage);

            // Register controllers
            System.out.println("\n🔌 Registering routes:");
            registerControllers();

            // Start HTTP server
            System.out.println("\n🚀 Starting HTTP server...");
            startHttpServer();

            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║   ✅ Application started successfully!             ║");
            System.out.println("║   🌐 Server running at: http://localhost:" + port + "      ║");
            System.out.println("║   📝 Press Ctrl+C to stop                         ║");
            System.out.println("╚════════════════════════════════════════════════════╝\n");

        } catch (Exception e) {
            System.err.println("\n❌ Failed to start application:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static int parsePort(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--port=")) {
                return Integer.parseInt(arg.substring(7));
            }
        }
        return 8080;
    }

    private static String getBasePackage(Class<?> applicationClass) {
        if (applicationClass.isAnnotationPresent(WebApplication.class)) {
            WebApplication annotation = applicationClass.getAnnotation(WebApplication.class);
            if (!annotation.basePackage().isEmpty()) {
                return annotation.basePackage();
            }
        }
        return applicationClass.getPackage().getName();
    }

    private static void scanAndRegister(String basePackage) {
        PackageScanner scanner = new PackageScanner();
        Set<Class<?>> classes = scanner.scanPackage(basePackage);

        System.out.println("  Found " + classes.size() + " components:");

        for (Class<?> clazz : classes) {
            String type = getComponentType(clazz);
            System.out.println("    ├─ " + type + ": " + clazz.getSimpleName());
            container.register(clazz);
        }
    }

    private static String getComponentType(Class<?> clazz) {
        if (clazz.isAnnotationPresent(RestController.class)) return "RestController";
        if (clazz.isAnnotationPresent(Controller.class)) return "Controller";
        if (clazz.isAnnotationPresent(Service.class)) return "Service";
        if (clazz.isAnnotationPresent(Repository.class)) return "Repository";
        if (clazz.isAnnotationPresent(Component.class)) return "Component";
        return "Unknown";
    }

    private static void registerControllers() {
        for (Object instance : container.getAllInstances()) {
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(RestController.class) ||
                    clazz.isAnnotationPresent(Controller.class)) {
                router.registerController(instance);
            }
        }
    }

    private static void startHttpServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", exchange -> {
            try {
                // Parse request
                HttpRequest request = parseRequest(exchange);

                // Handle request
                HttpResponse response = router.handleRequest(request);

                // Send response
                sendResponse(exchange, response);

            } catch (Exception e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        });

        server.setExecutor(null);
        server.start();
    }

    private static HttpRequest parseRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // Parse query parameters
        Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

        // Read body
        String body = null;
        if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
            body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        }

        // Get headers
        Map<String, String> headers = new HashMap<>();
        exchange.getRequestHeaders().forEach((key, values) -> {
            if (!values.isEmpty()) {
                headers.put(key, values.get(0));
            }
        });

        return new HttpRequest(method, path, new HashMap<>(), queryParams, body, headers);
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=", 2);
                if (pair.length == 2) {
                    params.put(pair[0], pair[1]);
                }
            }
        }
        return params;
    }

    private static void sendResponse(HttpExchange exchange, HttpResponse response) throws IOException {
        // Set headers
        exchange.getResponseHeaders().set("Content-Type", response.getContentType());
        response.getHeaders().forEach((key, value) ->
                exchange.getResponseHeaders().set(key, value));

        // Send response
        byte[] bytes = response.getBody() != null ?
                response.getBody().getBytes(StandardCharsets.UTF_8) : new byte[0];

        exchange.sendResponseHeaders(response.getStatusCode(), bytes.length);

        if (bytes.length > 0) {
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }

        exchange.close();
    }

    private static void sendErrorResponse(HttpExchange exchange, int status, String message) throws IOException {
        String body = "{\"error\": \"" + message + "\"}";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
        exchange.close();
    }

    public static void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Server stopped.");
        }
    }
}