package com.vcinsidedigital.webcore.extensibility;

import com.vcinsidedigital.webcore.http.HttpRequest;
import java.util.Map;

/**
 * Context provided to parameter handlers for resolving values
 */
public class ParameterContext {
    private final HttpRequest request;

    public ParameterContext(HttpRequest request) {
        this.request = request;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public String getPathParam(String name) {
        return request.getPathParams().get(name);
    }

    public String getQueryParam(String name) {
        return request.getQueryParams().get(name);
    }

    public String getHeader(String name) {
        return request.getHeaders().get(name);
    }

    public String getBody() {
        return request.getBody();
    }

    public Map<String, String> getAllHeaders() {
        return request.getHeaders();
    }
}
