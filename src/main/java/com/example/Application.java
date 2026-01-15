package com.example;

import com.vcinsidedigital.webcore.WebServerApplication;
import com.vcinsidedigital.webcore.annotations.WebApplication;

@WebApplication
public class Application extends WebServerApplication
{
    public static void main(String[] args) {
        WebServerApplication.run(Application.class, args);
    }
}