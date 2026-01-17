package com.example.plugins.customserver.gateway;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.vcinsidedigital.webcore.server.Gateway;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionGateway implements Gateway {

    private final Map<String, Map<String, Object>> sessions = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "Session Gateway";
    }

    @Override
    public void initialize(HttpServer server) throws Exception {
        server.createContext("/session", exchange -> {
            String sessionId = getSessionId(exchange);

            if (sessionId == null) {
                sessionId = UUID.randomUUID().toString();
                sessions.put(sessionId, new HashMap<>());

                exchange.getResponseHeaders().add("Set-Cookie", "SESSIONID=" + sessionId);
            }

            Map<String, Object> session = sessions.get(sessionId);
            String response = "{\"sessionId\": \"" + sessionId + "\", \"data\": " + session + "}";

            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        });
    }

    @Override
    public void onStart() {
        System.out.println("    └─ Session management active");
    }

    @Override
    public void onStop() {
        sessions.clear();
        System.out.println("    └─ Sessions cleared");
    }

    private String getSessionId(HttpExchange exchange) {
        String cookie = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookie != null) {
            for (String c : cookie.split(";")) {
                String[] parts = c.trim().split("=", 2);
                if (parts.length == 2 && parts[0].equals("SESSIONID")) {
                    return parts[1];
                }
            }
        }
        return null;
    }
}
