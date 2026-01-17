package com.vcinsidedigital.webcore.server;

import com.vcinsidedigital.webcore.http.HttpRequest;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.sun.net.httpserver.HttpExchange;

/**
 * Allows plugins to customize server configuration
 */
public interface ServerConfiguration {

    /**
     * Get custom port (returns null to use default/args)
     */
    default Integer getPort() {
        return null;
    }

    /**
     * Get custom host (returns null to use default/args)
     */
    default String getHost() {
        return null;
    }

    /**
     * Customize the HttpRequest parsing
     * Return null to use default implementation
     */
    default HttpRequest customizeRequest(HttpExchange exchange) throws Exception {
        return null;
    }

    /**
     * Customize the HttpResponse before sending
     * Can modify headers, body, status, etc
     */
    default void customizeResponse(HttpResponse response, HttpExchange exchange) {
        // Default: no customization
    }
}
