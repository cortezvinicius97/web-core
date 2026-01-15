package com.vcinsidedigital.webcore.http;


import java.util.*;

public class HttpRequest {
    private final String method;
    private final String path;
    private final Map<String, String> pathParams;
    private final Map<String, String> queryParams;
    private final String body;
    private final Map<String, String> headers;

    public HttpRequest(String method, String path, Map<String, String> pathParams,
                       Map<String, String> queryParams, String body, Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.pathParams = pathParams;
        this.queryParams = queryParams;
        this.body = body;
        this.headers = headers;
    }

    public String getMethod() { return method; }
    public String getPath() { return path; }
    public Map<String, String> getPathParams() { return pathParams; }
    public Map<String, String> getQueryParams() { return queryParams; }
    public String getBody() { return body; }
    public Map<String, String> getHeaders() { return headers; }
}