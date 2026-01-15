package com.example.middleware;

import com.vcinsidedigital.webcore.http.HttpRequest;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.vcinsidedigital.webcore.middleware.MiddlewareHandler;

public class LoggingMiddleware implements MiddlewareHandler {

    @Override
    public HttpResponse handle(HttpRequest request) {
        System.out.println("========================================");
        System.out.println("📝 Request Log:");
        System.out.println("   Method: " + request.getMethod());
        System.out.println("   Path: " + request.getPath());
        System.out.println("   Headers: " + request.getHeaders());
        System.out.println("========================================");

        // Continue to next middleware or controller
        return null;
    }
}
