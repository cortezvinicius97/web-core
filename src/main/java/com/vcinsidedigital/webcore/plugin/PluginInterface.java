package com.vcinsidedigital.webcore.plugin;

import com.sun.net.httpserver.HttpServer;
import com.vcinsidedigital.webcore.WebServerApplication;
import com.vcinsidedigital.webcore.routing.Router;
import com.vcinsidedigital.webcore.server.ServerConfiguration;

public interface PluginInterface {

    /**
     * Get unique plugin identifier
     * This ID must be unique across all plugins
     * @return unique plugin ID (e.g., "com.example.myplugin")
     */
    String getId();

    /**
     * Indicates if this plugin will handle server initialization
     * @return true if plugin will start the server, false otherwise
     */
    boolean isInitializeServer();

    /**
     * Called when the plugin is loaded
     * @param application The web application instance
     */
    void onLoad(WebServerApplication application);

    /**
     * Called when the application starts (after all components are registered)
     * @param application The web application instance
     */
    void onStart(WebServerApplication application);

    /**
     * Get the plugin name
     */
    String getName();

    /**
     * Get the plugin version
     */
    String getVersion();

    /**
     * Get the base package to scan for plugin components
     * @return the base package name (e.g., "com.exampleplugin")
     */
    default String getBasePackage() {
        return this.getClass().getPackage().getName();
    }

    /**
     * Called to initialize the HTTP server (only if isInitializeServer() returns true)
     * @param router The router instance
     * @param args Command line arguments
     * @param port The configured port
     */
    default void onServerInit(Router router, String[] args, String hostname, int port) {
        // Default implementation does nothing
    }

    /**
     * Provide server configuration customizations
     * Return null if plugin doesn't customize the server
     */
    default ServerConfiguration getServerConfiguration() {
        return null;
    }
}
