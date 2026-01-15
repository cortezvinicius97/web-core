package com.example.middleware;

import com.vcinsidedigital.webcore.http.HttpRequest;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.vcinsidedigital.webcore.middleware.MiddlewareHandler;

public class AuthMiddleware implements MiddlewareHandler {
    @Override
    public HttpResponse handle(HttpRequest request) {
        String authHeader = request.getHeaders().get("Authorization");

        if (authHeader == null || authHeader.isEmpty()) {
            return new HttpResponse()
                    .status(401)
                    .body("{\"error\": \"Authorization header is required\"}")
                    .contentType("application/json");
        }

        if (!authHeader.startsWith("Bearer ")) {
            return new HttpResponse()
                    .status(401)
                    .body("{\"error\": \"Invalid authorization format. Use: Bearer <token>\"}")
                    .contentType("application/json");
        }

        // Simulate token validation
        String token = authHeader.substring(7);
        if (!"valid-token-123".equals(token)) {
            return new HttpResponse()
                    .status(403)
                    .body("{\"error\": \"Invalid or expired token\"}")
                    .contentType("application/json");
        }

        // Token is valid, continue to controller
        return null;
    }
}
