package com.vcinsidedigital.webcore.plugin;

import com.sun.net.httpserver.HttpServer;
import com.vcinsidedigital.webcore.WebServerApplication;
import com.vcinsidedigital.webcore.routing.Router;
import com.vcinsidedigital.webcore.server.ServerConfiguration;

public abstract class AbstractPlugin implements PluginInterface
{
    @Override
    public void onLoad(WebServerApplication application) {

    }

    @Override
    public void onStart(WebServerApplication application) {

    }

    @Override
    public void onServerInit(Router router, String[] args, String hostname, int port) {
        // Default implementation
    }

    @Override
    public void onStop(WebServerApplication application) {

    }

    @Override
    public String getId() {
        // Default: use fully qualified class name as ID
        return this.getClass().getName();
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean isInitializeServer() {
        return false;
    }

    @Override
    public String getBasePackage() {
        return PluginInterface.super.getBasePackage();
    }

    @Override
    public ServerConfiguration getServerConfiguration() {
        return PluginInterface.super.getServerConfiguration();
    }
}
