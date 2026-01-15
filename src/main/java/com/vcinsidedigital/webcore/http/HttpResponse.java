package com.vcinsidedigital.webcore.http;
import java.util.*;


public class HttpResponse {
    private int statusCode;
    private String body;
    private String contentType;
    private Map<String, String> headers;

    public HttpResponse() {
        this.statusCode = 200;
        this.contentType = "application/json";
        this.headers = new HashMap<>();
    }

    public HttpResponse status(int code) {
        this.statusCode = code;
        return this;
    }

    public HttpResponse body(String body) {
        this.body = body;
        return this;
    }

    public HttpResponse contentType(String type) {
        this.contentType = type;
        return this;
    }

    public HttpResponse header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public int getStatusCode() { return statusCode; }
    public String getBody() { return body; }
    public String getContentType() { return contentType; }
    public Map<String, String> getHeaders() { return headers; }
}

