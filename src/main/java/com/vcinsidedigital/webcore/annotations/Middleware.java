package com.vcinsidedigital.webcore.annotations;

import com.vcinsidedigital.webcore.middleware.MiddlewareHandler;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Middleware {
    Class<? extends MiddlewareHandler>[] value();
}

