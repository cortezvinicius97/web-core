package com.example.controller;

import com.vcinsidedigital.webcore.annotations.*;

@RestController
public class ProductController
{
    @Get("/products")
    public String getAllProducts() {
        return "{\"products\": [\"Product 1\", \"Product 2\"]}";
    }

    @Get("/products/{id}")
    public String getProduct(@Path("id") Long id) {
        return "{\"id\": " + id + ", \"name\": \"Product " + id + "\"}";
    }

    @Post("/products")
    public String createProduct(@Body String body) {
        return "{\"message\": \"Product created\", \"data\": " + body + "}";
    }
}
