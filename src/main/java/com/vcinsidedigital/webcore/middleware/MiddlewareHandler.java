package com.vcinsidedigital.webcore.middleware;

import com.vcinsidedigital.webcore.http.HttpRequest;
import com.vcinsidedigital.webcore.http.HttpResponse;

public interface MiddlewareHandler {
    /**
     * Executes middleware logic before the controller method
     * @param request The HTTP request
     * @return null to continue to next middleware/controller, or HttpResponse to short-circuit
     */
    HttpResponse handle(HttpRequest request);
}
