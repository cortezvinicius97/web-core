package com.vcinsidedigital.webcore.middleware;

import com.vcinsidedigital.webcore.http.HttpRequest;


public class MiddlewareContext {
    private final HttpRequest request;

    public MiddlewareContext(HttpRequest request) {
        this.request = request;
    }

    public HttpRequest getRequest() {
        return request;
    }
}