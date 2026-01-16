package com.example.plugins.hello;


import com.vcinsidedigital.webcore.WebServerApplication;
import com.vcinsidedigital.webcore.annotations.Plugin;
import com.vcinsidedigital.webcore.plugin.AbstractPlugin;

@Plugin
public class HelloPlugin extends AbstractPlugin
{

    @Override
    public String getId() {
        return "com.example.plugins.hello";
    }


    @Override
    public String getName() {
        return "Hello Plugin";
    }

    @Override
    public String getVersion() {
        return super.getVersion();
    }

    @Override
    public void onLoad(WebServerApplication application) {

        System.out.println("    ├─ Hello Plugin loaded");
    }

    @Override
    public boolean isInitializeServer() {
        return false;
    }

    @Override
    public void onStart(WebServerApplication application) {
        System.out.println("    ├─ Hello plugin started");
        System.out.println("    └─ Available Middleware Hello");
    }
}
