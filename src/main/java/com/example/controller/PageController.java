package com.example.controller;

import com.vcinsidedigital.webcore.annotations.Controller;
import com.vcinsidedigital.webcore.annotations.Get;
import com.vcinsidedigital.webcore.annotations.ResponseStatus;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.vcinsidedigital.webcore.http.HttpStatus;

@Controller
public class PageController
{
    @Get("/")
    public HttpResponse home() {
        String html = "<h1>Welcome Home</h1><p>This is the home page</p>";
        return new HttpResponse().contentType("text/html; charset=utf-8").status(200).body(html);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @Get("/error")
    public HttpResponse errorPage() {
        String html = "<h1>404 - Page Not Found</h1><p>The page you are looking for does not exist.</p>";
        return new HttpResponse().contentType("text/html; charset=utf-8").body(html);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @Get("/unauthorized")
    public HttpResponse unauthorized() {
        String html = "<h1>401 - Unauthorized</h1><p>You need to login to access this page.</p>";
        return new HttpResponse().contentType("text/html; charset=utf-8").body(html);
    }

    @Get("/about")
    public HttpResponse about() {
        String html = "<h1>About Us</h1><p>We are a company that does things.</p>";
        return new HttpResponse().contentType("text/html; charset=utf-8").body(html);
    }
}
