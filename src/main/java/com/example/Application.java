package com.example;

import com.externalplugin.annotations.PluginAnnotations;
import com.example.plugins.hello.HelloPlugin;
import com.vcinsidedigital.webcore.WebServerApplication;
import com.vcinsidedigital.webcore.annotations.WebApplication;

@WebApplication
public class Application extends WebServerApplication
{
    public static void main(String[] args) {
        registerPlugin(new PluginAnnotations());
        WebServerApplication.run(Application.class, args);
    }
}