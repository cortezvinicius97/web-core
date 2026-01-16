package com.example.plugins.hello.middleware;

import com.vcinsidedigital.webcore.http.HttpRequest;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.vcinsidedigital.webcore.middleware.MiddlewareHandler;

public class HelloPluginMiddleware implements MiddlewareHandler {
    @Override
    public HttpResponse handle(HttpRequest request) {
        String hello  = request.getHeaders().get("hello");
        if (hello == null || hello.isEmpty()) {
            return new HttpResponse()
                    .status(401)
                    .body("{\"error\": \"Invalid\"}")
                    .contentType("application/json");
        }
        return null;
    }
}
