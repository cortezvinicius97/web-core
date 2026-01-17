package com.externalplugin.annotations.handlers;

import com.externalplugin.annotations.annotation.Cookie;
import com.vcinsidedigital.webcore.extensibility.ParameterAnnotationHandler;
import com.vcinsidedigital.webcore.extensibility.ParameterContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

public class CookieParameterHandler implements ParameterAnnotationHandler {
    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return Cookie.class;
    }

    @Override
    public boolean canHandle(Parameter parameter) {
        return parameter.isAnnotationPresent(Cookie.class);
    }

    @Override
    public Object resolveParameter(Parameter parameter, ParameterContext context) throws Exception {
        Cookie cookie = parameter.getAnnotation(Cookie.class);
        String cookieName = cookie.value();

        // Parse cookies from Cookie header
        String cookieHeader = context.getHeader("Cookie");
        if (cookieHeader != null) {
            String[] cookies = cookieHeader.split(";");
            for (String c : cookies) {
                String[] parts = c.trim().split("=", 2);
                if (parts.length == 2 && parts[0].equals(cookieName)) {
                    return parts[1];
                }
            }
        }

        return null;
    }
}
