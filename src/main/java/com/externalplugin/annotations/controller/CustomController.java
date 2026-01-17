package com.externalplugin.annotations.controller;

import com.externalplugin.annotations.annotation.ApiController;
import com.externalplugin.annotations.annotation.Cookie;
import com.externalplugin.annotations.annotation.Header;
import com.vcinsidedigital.webcore.annotations.Get;
import com.vcinsidedigital.webcore.annotations.Path;
import com.vcinsidedigital.webcore.annotations.Query;
import com.vcinsidedigital.webcore.annotations.RestController;
import com.vcinsidedigital.webcore.http.HttpResponse;


@ApiController
public class CustomController {

    @Get("/custom/headers")
    public HttpResponse testHeaders(
            @Header("Authorization") String auth,
            @Header(value = "X-Custom-Header", required = false) String custom
    ) {
        return new HttpResponse()
                .status(200)
                .body("{\"auth\": \"" + auth + "\", \"custom\": \"" + custom + "\"}");
    }

    @Get("/custom/cookies")
    public HttpResponse testCookies(
            @Cookie("session") String sessionId,
            @Cookie("theme") String theme
    ) {
        return new HttpResponse()
                .status(200)
                .body("{\"session\": \"" + sessionId + "\", \"theme\": \"" + theme + "\"}");
    }

    @Get("/custom/mixed")
    public HttpResponse testMixed(
            @Path("id") Long id,
            @Query("name") String name,
            @Header("User-Agent") String userAgent
    ) {
        return new HttpResponse()
                .status(200)
                .body("{\"id\": " + id + ", \"name\": \"" + name + "\", \"userAgent\": \"" + userAgent + "\"}");
    }
}
