package com.example.plugins.hello.controller;

import com.vcinsidedigital.webcore.annotations.Get;
import com.vcinsidedigital.webcore.annotations.RestController;
import com.vcinsidedigital.webcore.http.HttpResponse;

@RestController(path = "/api/plugin")
public class HelloPluginController
{
    @Get("/")
    public HttpResponse helloPlugin(){
        return new HttpResponse().contentType("application/json").status(200).body("{\"plugin: \":\"Hello Plugin\", \"version\":\"1.0.0\"}");
    }
}
