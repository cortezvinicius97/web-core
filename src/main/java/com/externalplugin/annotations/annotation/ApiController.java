package com.externalplugin.annotations.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApiController {
    String path() default "/api";
    String version() default "v1";
}