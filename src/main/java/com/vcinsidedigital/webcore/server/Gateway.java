package com.vcinsidedigital.webcore.server;

import com.sun.net.httpserver.HttpServer;

/**
 * Gateway for adding server capabilities (WebSocket, Sessions, etc)
 */
public interface Gateway {

    /**
     * Get gateway name for logging
     */
    String getName();

    /**
     * Initialize the gateway with the HTTP server
     * Add contexts, filters, or other configurations here
     */
    void initialize(HttpServer server) throws Exception;

    /**
     * Called when server starts
     */
    default void onStart() {
        // Optional hook
    }

    /**
     * Called when server stops
     */
    default void onStop() {
        // Optional hook
    }
}
