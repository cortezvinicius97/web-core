package com.vcinsidedigital.webcore.server;

import com.vcinsidedigital.webcore.http.HttpRequest;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.sun.net.httpserver.HttpExchange;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry for server customizations and gateways
 */
public class ServerCustomizer {
    private static final ServerCustomizer INSTANCE = new ServerCustomizer();

    private ServerConfiguration portConfig = null;
    private ServerConfiguration hostConfig = null;
    private ServerConfiguration requestConfig = null;
    private ServerConfiguration responseConfig = null;

    private final List<Gateway> gateways = new CopyOnWriteArrayList<>();

    private ServerCustomizer() {}

    public static ServerCustomizer getInstance() {
        return INSTANCE;
    }

    /**
     * Register port customization
     * @throws IllegalStateException if port is already customized by another plugin
     */
    public void registerPortCustomization(ServerConfiguration config, String pluginName) {
        if (config.getPort() != null) {
            if (portConfig != null) {
                throw new IllegalStateException(
                        "Port is already customized by another plugin. " +
                                "Only one plugin can customize the port."
                );
            }
            portConfig = config;
            System.out.println("  ✅ Port customization registered by: " + pluginName);
        }
    }

    /**
     * Register host customization
     * @throws IllegalStateException if host is already customized by another plugin
     */
    public void registerHostCustomization(ServerConfiguration config, String pluginName) {
        if (config.getHost() != null) {
            if (hostConfig != null) {
                throw new IllegalStateException(
                        "Host is already customized by another plugin. " +
                                "Only one plugin can customize the host."
                );
            }
            hostConfig = config;
            System.out.println("  ✅ Host customization registered by: " + pluginName);
        }
    }

    /**
     * Register request customization
     * @throws IllegalStateException if request is already customized by another plugin
     */
    public void registerRequestCustomization(ServerConfiguration config, String pluginName) {
        if (requestConfig != null) {
            throw new IllegalStateException(
                    "Request parsing is already customized by another plugin. " +
                            "Only one plugin can customize request parsing."
            );
        }
        requestConfig = config;
        System.out.println("  ✅ Request customization registered by: " + pluginName);
    }

    /**
     * Register response customization
     * @throws IllegalStateException if response is already customized by another plugin
     */
    public void registerResponseCustomization(ServerConfiguration config, String pluginName) {
        if (responseConfig != null) {
            throw new IllegalStateException(
                    "Response handling is already customized by another plugin. " +
                            "Only one plugin can customize response handling."
            );
        }
        responseConfig = config;
        System.out.println("  ✅ Response customization registered by: " + pluginName);
    }

    /**
     * Register a gateway (multiple gateways allowed)
     */
    public void registerGateway(Gateway gateway) {
        if (!gateways.contains(gateway)) {
            gateways.add(gateway);
            System.out.println("  ✅ Gateway registered: " + gateway.getName());
        }
    }

    // Getters

    public Integer getCustomPort() {
        return portConfig != null ? portConfig.getPort() : null;
    }

    public String getCustomHost() {
        return hostConfig != null ? hostConfig.getHost() : null;
    }

    public HttpRequest customizeRequest(HttpExchange exchange) throws Exception {
        if (requestConfig != null) {
            return requestConfig.customizeRequest(exchange);
        }
        return null;
    }

    public void customizeResponse(HttpResponse response, HttpExchange exchange) {
        if (responseConfig != null) {
            responseConfig.customizeResponse(response, exchange);
        }
    }

    public List<Gateway> getGateways() {
        return new ArrayList<>(gateways);
    }

    public void clear() {
        portConfig = null;
        hostConfig = null;
        requestConfig = null;
        responseConfig = null;
        gateways.clear();
    }
}
