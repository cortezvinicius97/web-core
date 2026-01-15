package com.vcinsidedigital.webcore.annotations;

import com.vcinsidedigital.webcore.http.HttpStatus;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResponseStatus {
    HttpStatus value();
}
