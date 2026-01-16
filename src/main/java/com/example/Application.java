package com.example;

import com.example.plugins.hello.HelloPlugin;
import com.example.plugins.startServer.StartServer;
import com.vcinsidedigital.webcore.WebServerApplication;
import com.vcinsidedigital.webcore.annotations.WebApplication;

@WebApplication
public class Application extends WebServerApplication
{
    public static void main(String[] args) {
        registerPlugin(new HelloPlugin());
        registerPlugin(new StartServer());
        WebServerApplication.run(Application.class, args);
    }
}