package com.vcinsidedigital.webcore.extensibility;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * Handler for custom parameter annotations
 * Plugins can implement this to add custom parameter binding
 */
public interface ParameterAnnotationHandler {
    /**
     * Get the annotation class this handler recognizes
     */
    Class<? extends Annotation> getAnnotationType();

    /**
     * Check if this handler can handle the given parameter
     */
    boolean canHandle(Parameter parameter);

    /**
     * Extract and convert the parameter value from the request context
     */
    Object resolveParameter(Parameter parameter, ParameterContext context) throws Exception;
}
