package com.example.controller;


import com.example.middleware.AuthMiddleware;
import com.example.middleware.LoggingMiddleware;
import com.example.middleware.RateLimitMiddleware;
import com.vcinsidedigital.webcore.annotations.*;
import com.vcinsidedigital.webcore.http.HttpStatus;

@RestController
@Middleware({LoggingMiddleware.class, AuthMiddleware.class})
public class SecureController {

    @Get("/secure/profile")
    public String getProfile() {
        return "{\"user\": \"John Doe\", \"email\": \"john@example.com\"}";
    }

    @Get("/secure/data")
    public String getData() {
        return "{\"data\": \"sensitive information\"}";
    }

    // Method-level middleware applies ONLY to this method
    @Middleware({RateLimitMiddleware.class})
    @Get("/secure/limited")
    public String getLimitedResource() {
        return "{\"message\": \"This endpoint has rate limiting\"}";
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Post("/secure/create")
    public String createSecure(@Body String data) {
        return "{\"message\": \"Created\", \"data\": " + data + "}";
    }
}
