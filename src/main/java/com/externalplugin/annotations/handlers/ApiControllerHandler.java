package com.externalplugin.annotations.handlers;

import com.externalplugin.annotations.annotation.ApiController;
import com.vcinsidedigital.webcore.extensibility.ComponentAnnotationHandler;

import java.lang.annotation.Annotation;

public class ApiControllerHandler implements ComponentAnnotationHandler {
    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ApiController.class;
    }

    @Override
    public boolean isComponent(Class<?> clazz) {
        return clazz.isAnnotationPresent(ApiController.class);
    }

    @Override
    public boolean isController(Class<?> clazz) {
        return true;
    }

    @Override
    public String getBasePath(Class<?> clazz) {
        if (clazz.isAnnotationPresent(ApiController.class)) {
            ApiController annotation = clazz.getAnnotation(ApiController.class);
            String version = annotation.version();
            String path = annotation.path();

            // Build base path from version (e.g., "v2" -> "/v2")
            if (version != null && !version.isEmpty()) {
                return path+"/"+version;
            }
        }
        return "";
    }
}
