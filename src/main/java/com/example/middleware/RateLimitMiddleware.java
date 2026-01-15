package com.example.middleware;

import com.vcinsidedigital.webcore.http.HttpRequest;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.vcinsidedigital.webcore.middleware.MiddlewareHandler;

public class RateLimitMiddleware implements MiddlewareHandler {
    private static final java.util.Map<String, Integer> requestCounts = new java.util.HashMap<>();
    private static final int MAX_REQUESTS = 10;

    @Override
    public HttpResponse handle(HttpRequest request) {
        String clientIp = request.getHeaders().getOrDefault("X-Forwarded-For", "127.0.0.1");

        int count = requestCounts.getOrDefault(clientIp, 0);

        if (count >= MAX_REQUESTS) {
            return new HttpResponse()
                    .status(429)
                    .body("{\"error\": \"Too many requests. Please try again later.\"}")
                    .contentType("application/json");
        }

        requestCounts.put(clientIp, count + 1);

        // Continue to controller
        return null;
    }
}
