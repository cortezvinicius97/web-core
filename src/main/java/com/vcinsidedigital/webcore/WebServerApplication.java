package com.vcinsidedigital.webcore;

import com.vcinsidedigital.webcore.annotations.*;
import com.vcinsidedigital.webcore.core.*;
import com.vcinsidedigital.webcore.extensibility.AnnotationHandlerRegistry;
import com.vcinsidedigital.webcore.extensibility.ComponentAnnotationHandler;
import com.vcinsidedigital.webcore.plugin.DuplicatePluginException;
import com.vcinsidedigital.webcore.plugin.PluginInterface;
import com.vcinsidedigital.webcore.plugin.PluginManager;
import com.vcinsidedigital.webcore.routing.Router;
import com.vcinsidedigital.webcore.http.*;
import com.sun.net.httpserver.*;
import com.vcinsidedigital.webcore.server.Gateway;
import com.vcinsidedigital.webcore.server.ServerCustomizer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class WebServerApplication {

    private static DIContainer container;
    private static Router router;
    private static HttpServer server;
    private static final PluginManager pluginManager = new PluginManager();
    private static int port = 8080;
    private static String hostName = "localhost";

    public static void run(Class<?> applicationClass, String[] args) {
        try {
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║         WEB FRAMEWORK - Starting Application       ║");
            System.out.println("╚════════════════════════════════════════════════════╝\n");

            // Parse port from args
            port = parsePort(args);
            hostName = parseHost(args);

            // Initialize container, router and plugin manager
            container = new DIContainer();
            router = new Router();

            // Get base package
            String basePackage = getBasePackage(applicationClass);

            // ===== FASE 1: Descobrir e registrar APENAS plugins =====
            System.out.println("📦 Discovering plugins in: " + basePackage);
            discoverAndRegisterPlugins(basePackage);

            // ===== FASE 2: Carregar plugins (registra handlers customizados) =====
            pluginManager.loadPlugins(getInstance());

            // ===== FASE 3: Escanear pacote principal NOVAMENTE (agora com handlers registrados) =====
            System.out.println("\n📦 Scanning package: " + basePackage);
            scanAndRegister(basePackage);

            // ===== FASE 4: Escanear pacotes dos plugins =====
            List<String> pluginPackages = pluginManager.getPluginPackages();
            if (!pluginPackages.isEmpty()) {
                System.out.println("\n📦 Scanning plugin packages:");
                for (String pluginPackage : pluginPackages) {
                    System.out.println("  Scanning: " + pluginPackage);
                    scanAndRegister(pluginPackage);
                }
            }

            // Register controllers (from both app and plugins)
            System.out.println("\n🔌 Registering routes:");
            registerControllers();

            // Start plugins
            pluginManager.startPlugins(getInstance());

            // Check if any plugin wants to handle server initialization
            if (pluginManager.hasServerInitializer()) {
                System.out.println("\n🚀 Starting HTTP server via plugin...");
                pluginManager.initializeServer(router, args, hostName, port);
            } else {
                // Start HTTP server with default implementation
                System.out.println("\n🚀 Starting HTTP server...");
                startHttpServer();
            }
            stop();
        } catch (Exception e) {
            System.err.println("\n❌ Failed to start application:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static WebServerApplication getInstance() {
        return new WebServerApplication() {};
    }

    public static void registerPlugin(PluginInterface plugin) {
        pluginManager.registerPlugin(plugin);
    }

    public static void registerPlugin(Class<? extends PluginInterface> pluginClass) {
        pluginManager.registerPlugin(pluginClass);
    }

    public static DIContainer getContainer() {
        return container;
    }

    public static Router getRouter() {
        return router;
    }

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    public static int getPort() {
        return port;
    }

    private static int parsePort(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--port=")) {
                return Integer.parseInt(arg.substring(7));
            }
        }
        return 8080;
    }

    private static String parseHost(String[] args){
        for (String arg:args){
            if (arg.startsWith("--host=")){
                return arg.substring(7);
            }
        }
        return "localhost";
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

    /**
     * Fase 1: Descobre e registra APENAS plugins (não registra outros componentes ainda)
     */
    private static void discoverAndRegisterPlugins(String basePackage) {
        PackageScanner scanner = new PackageScanner();
        Set<Class<?>> classes = scanner.scanPackage(basePackage);

        List<Class<?>> pluginClasses = new ArrayList<>();

        // Filtrar APENAS plugins
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Plugin.class) && PluginInterface.class.isAssignableFrom(clazz)) {
                pluginClasses.add(clazz);
            }
        }

        System.out.println("  Found " + pluginClasses.size() + " plugin(s):");

        // Registrar todos os plugins
        for (Class<?> clazz : pluginClasses) {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends PluginInterface> pluginClass = (Class<? extends PluginInterface>) clazz;

                if (!pluginManager.isPluginRegistered(pluginClass)) {
                    try {
                        pluginManager.registerPlugin(pluginClass);
                    } catch (DuplicatePluginException e) {
                        System.err.println("    ├─ ❌ " + e.getMessage());
                        // Mark this package as failed
                        try {
                            PluginInterface failedPlugin = pluginClass.getDeclaredConstructor().newInstance();
                            pluginManager.markPackageAsFailed(failedPlugin.getBasePackage());
                        } catch (Exception ex) {
                            pluginManager.markPackageAsFailed(clazz.getPackage().getName());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("    ├─ ❌ Failed to register plugin: " + clazz.getSimpleName());
                e.printStackTrace();
            }
        }
    }

    private static void scanAndRegister(String basePackage) {
        PackageScanner scanner = new PackageScanner();
        Set<Class<?>> classes = scanner.scanPackage(basePackage);

        System.out.println("  Found " + classes.size() + " components:");

        List<Class<?>> regularComponents = new ArrayList<>();

        // Filtrar APENAS componentes regulares (não plugins)
        for (Class<?> clazz : classes) {
            // IGNORAR plugins (eles já foram registrados na fase 1)
            if (clazz.isAnnotationPresent(Plugin.class) && PluginInterface.class.isAssignableFrom(clazz)) {
                continue;
            }
            regularComponents.add(clazz);
        }

        // Registrar apenas componentes que NÃO são de pacotes com falha
        for (Class<?> clazz : regularComponents) {
            String componentPackage = clazz.getPackage().getName();

            // Check if this component belongs to a failed plugin package
            if (pluginManager.isPackageFailed(componentPackage)) {
                System.out.println("    ├─ ⏭️  Skipped (failed plugin): " + clazz.getSimpleName());
                continue;
            }

            String type = scanner.getComponentType(clazz);
            System.out.println("    ├─ " + type + ": " + clazz.getSimpleName());
            container.register(clazz);
        }
    }

    private static void registerControllers() {
        PackageScanner scanner = new PackageScanner();

        for (Object instance : container.getAllInstances()) {
            Class<?> clazz = instance.getClass();

            // Check built-in controller annotations
            boolean isController = clazz.isAnnotationPresent(RestController.class) ||
                    clazz.isAnnotationPresent(Controller.class);

            // Check custom controller handlers
            if (!isController) {
                for (ComponentAnnotationHandler handler : AnnotationHandlerRegistry.getInstance().getComponentHandlers()) {
                    if (handler.isController(clazz)) {
                        isController = true;
                        break;
                    }
                }
            }

            if (isController) {
                router.registerController(instance);
            }
        }
    }

    private static void startHttpServer() throws IOException {
        // Apply custom port/host from plugins
        Integer customPort = ServerCustomizer.getInstance().getCustomPort();
        String customHost = ServerCustomizer.getInstance().getCustomHost();

        int finalPort = customPort != null ? customPort : port;
        String finalHost = customHost != null ? customHost : hostName;

        server = HttpServer.create(new InetSocketAddress(finalHost, finalPort), 0);

        // Initialize gateways
        List<Gateway> gateways = ServerCustomizer.getInstance().getGateways();
        if (!gateways.isEmpty()) {
            System.out.println("\n🔌 Initializing gateways:");
            for (Gateway gateway : gateways) {
                try {
                    gateway.initialize(server);
                    System.out.println("  ├─ " + gateway.getName() + " initialized");
                } catch (Exception e) {
                    System.err.println("  ├─ ❌ Failed to initialize gateway: " + gateway.getName());
                    e.printStackTrace();
                }
            }
        }

        server.createContext("/", exchange -> {
            try {
                // Custom request parsing
                HttpRequest request = ServerCustomizer.getInstance().customizeRequest(exchange);
                if (request == null) {
                    request = parseRequest(exchange); // Use default
                }

                HttpResponse response = router.handleRequest(request);

                // Custom response handling
                ServerCustomizer.getInstance().customizeResponse(response, exchange);

                sendResponse(exchange, response);
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        });

        server.setExecutor(null);
        server.start();

        // Start gateways
        for (Gateway gateway : gateways) {
            try {
                gateway.onStart();
            } catch (Exception e) {
                System.err.println("  ├─ ❌ Error starting gateway: " + gateway.getName());
                e.printStackTrace();
            }
        }

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║   ✅ Application started successfully!             ║");
        System.out.println("║   🌐 Server running at: http://" + finalHost + ":" + finalPort + "      ║");
        System.out.println("║   📝 Press Ctrl+C to stop                          ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
    }

    private static HttpRequest parseRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

        String body = null;
        if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
            body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        }

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

    public static void sendResponse(HttpExchange exchange, HttpResponse response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", response.getContentType());
        response.getHeaders().forEach((key, value) ->
                exchange.getResponseHeaders().set(key, value));

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

    public static void sendErrorResponse(HttpExchange exchange, int status, String message) throws IOException {
        String body = "{\"error\": \"" + message + "\"}";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
        exchange.close();
    }

    private static void stop() {
        if (server != null) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                server.stop(0);
                WebServerApplication app = getInstance();
                pluginManager.stopPlugins(app);
                System.out.println("Server stopped.");
            }));

        }
    }
}