package com.vcinsidedigital.webcore.extensibility;

import java.lang.annotation.Annotation;

/**
 * Handler for custom component annotations
 * Plugins can implement this to register custom component types
 */
public interface ComponentAnnotationHandler {
    /**
     * Get the annotation class this handler recognizes
     */
    Class<? extends Annotation> getAnnotationType();

    /**
     * Check if a class should be registered as a component
     */
    boolean isComponent(Class<?> clazz);

    /**
     * Check if this component should be registered as a controller
     * Default: false (not a controller)
     */
    default boolean isController(Class<?> clazz) {
        return false;
    }

    /**
     * Get the base path for controller routes
     * Only called if isController() returns true
     * @param clazz The controller class
     * @return base path (e.g., "/api", "/v2", ""), or null to use default behavior
     */
    default String getBasePath(Class<?> clazz) {
        return "";
    }
}
