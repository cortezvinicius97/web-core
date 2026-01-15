package com.example.controller;

import com.vcinsidedigital.webcore.annotations.Controller;
import com.vcinsidedigital.webcore.annotations.Get;
import com.vcinsidedigital.webcore.annotations.ResponseStatus;
import com.vcinsidedigital.webcore.http.HttpStatus;

@Controller
public class PageController
{
    @Get("/")
    public String home() {
        return "<h1>Welcome Home</h1><p>This is the home page</p>";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @Get("/error")
    public String errorPage() {
        return "<h1>404 - Page Not Found</h1><p>The page you are looking for does not exist.</p>";
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @Get("/unauthorized")
    public String unauthorized() {
        return "<h1>401 - Unauthorized</h1><p>You need to login to access this page.</p>";
    }

    @Get("/about")
    public String about() {
        return "<h1>About Us</h1><p>We are a company that does things.</p>";
    }
}
