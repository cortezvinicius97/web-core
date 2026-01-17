package com.externalplugin.startServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.vcinsidedigital.webcore.WebServerApplication;
import com.vcinsidedigital.webcore.annotations.Plugin;
import com.vcinsidedigital.webcore.http.HttpRequest;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.vcinsidedigital.webcore.plugin.AbstractPlugin;
import com.vcinsidedigital.webcore.routing.Router;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Plugin
public class StartServer extends AbstractPlugin
{

    @Override
    public String getId() {
        return "com.example.plugins.start-server";
    }

    @Override
    public String getName() {
        return "Start Server";
    }

    @Override
    public void onLoad(WebServerApplication application) {
        System.out.println("    ├─ Start Plugin loaded");
    }

    @Override
    public void onStart(WebServerApplication application) {
        System.out.println("    ├─ Start plugin started");
        System.out.println("    └─ Available Middleware Hello");
    }


    @Override
    public boolean isInitializeServer() {
        return true;
    }

    @Override
    public void onServerInit(Router router, String[] args, String hostname,int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", exchange -> {
                try {
                    // Parse request
                    HttpRequest request = parseRequest(exchange);

                    // Handle request
                    HttpResponse response = router.handleRequest(request);
                    response.header("Access-Control-Allow-Origin", "*")
                            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS")
                            .header("Access-Control-Allow-Headers", "Content-Type, Authorization");

                    // Send response
                    sendResponse(exchange, response);

                } catch (Exception e) {
                    e.printStackTrace();
                    sendErrorResponse(exchange, 500, "Internal Server Error");
                }
            });

            server.setExecutor(null);
            server.start();
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║   ✅ Application started successfully!             ║");
            System.out.println("║   🌐 Server running at: http://"+ hostname+":" + port + "      ║");
            System.out.println("║   📝 Press Ctrl+C to stop                          ║");
            System.out.println("╚════════════════════════════════════════════════════╝\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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


    private static int parsePort(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--port=")) {
                return Integer.parseInt(arg.substring(7));
            }
        }
        return 8080;
    }
}
