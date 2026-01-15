package com.example.middleware;

import com.vcinsidedigital.webcore.http.HttpRequest;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.vcinsidedigital.webcore.middleware.MiddlewareHandler;

public class CorsMiddleware implements MiddlewareHandler {

    @Override
    public HttpResponse handle(HttpRequest request) {
        // For OPTIONS requests (preflight), return immediately
        if ("OPTIONS".equals(request.getMethod())) {
            return new HttpResponse()
                    .status(200)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                    .body("");
        }

        // For other requests, continue but add CORS headers in response
        // (Note: In a real implementation, you'd modify the response after controller execution)
        return null;
    }
}
