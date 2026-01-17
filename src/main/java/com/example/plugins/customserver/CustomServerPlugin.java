package com.example.plugins.customserver;

import com.example.plugins.customserver.gateway.MetricsGateway;
import com.example.plugins.customserver.gateway.SessionGateway;
import com.sun.net.httpserver.HttpExchange;
import com.vcinsidedigital.webcore.WebServerApplication;
import com.vcinsidedigital.webcore.annotations.Plugin;
import com.vcinsidedigital.webcore.http.HttpRequest;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.vcinsidedigital.webcore.plugin.AbstractPlugin;
import com.vcinsidedigital.webcore.server.ServerConfiguration;
import com.vcinsidedigital.webcore.server.ServerCustomizer;

@Plugin
public class CustomServerPlugin extends AbstractPlugin
{
    @Override
    public void onLoad(WebServerApplication application) {
        ServerCustomizer.getInstance().registerGateway(new SessionGateway());
        ServerCustomizer.getInstance().registerGateway(new MetricsGateway());
    }

    @Override
    public void onStart(WebServerApplication application) {
        super.onStart(application);
    }

    @Override
    public String getId() {
        return "com.example.plugins.customserver";
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getVersion() {
        return super.getVersion();
    }

    @Override
    public ServerConfiguration getServerConfiguration() {
        return new ServerConfiguration() {
            @Override
            public Integer getPort() {
                return 9090;
            }

            @Override
            public String getHost() {
                return "localhost";
            }

            @Override
            public HttpRequest customizeRequest(HttpExchange exchange) throws Exception {
                return ServerConfiguration.super.customizeRequest(exchange);
            }

            @Override
            public void customizeResponse(HttpResponse response, HttpExchange exchange) {
                ServerConfiguration.super.customizeResponse(response, exchange);
            }
        };
    }
}
