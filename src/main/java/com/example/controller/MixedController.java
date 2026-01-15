package com.example.controller;

import com.example.middleware.LoggingMiddleware;
import com.example.middleware.AuthMiddleware;
import com.vcinsidedigital.webcore.annotations.*;

@RestController
@Middleware({LoggingMiddleware.class})  // All methods get logging
public class MixedController {

    // Public endpoint - only logging
    @Get("/public/info")
    public String publicInfo() {
        return "{\"message\": \"This is public\"}";
    }

    // Protected endpoint - logging + auth
    @Middleware({AuthMiddleware.class})
    @Get("/private/info")
    public String privateInfo() {
        return "{\"message\": \"This is private\"}";
    }
}
