package com.vcinsidedigital.webcore.extensibility;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry for custom annotation handlers
 * Allows plugins to register custom component and parameter annotations
 */
public class AnnotationHandlerRegistry {
    private static final AnnotationHandlerRegistry INSTANCE = new AnnotationHandlerRegistry();

    private final List<ComponentAnnotationHandler> componentHandlers = new CopyOnWriteArrayList<>();
    private final List<ParameterAnnotationHandler> parameterHandlers = new CopyOnWriteArrayList<>();

    private AnnotationHandlerRegistry() {}

    public static AnnotationHandlerRegistry getInstance() {
        return INSTANCE;
    }

    public void registerComponentHandler(ComponentAnnotationHandler handler) {
        if (!componentHandlers.contains(handler)) {
            componentHandlers.add(handler);
            System.out.println("  ✅ Registered component annotation handler: " +
                    handler.getAnnotationType().getSimpleName());
        }
    }

    public void registerParameterHandler(ParameterAnnotationHandler handler) {
        if (!parameterHandlers.contains(handler)) {
            parameterHandlers.add(handler);
            System.out.println("  ✅ Registered parameter annotation handler: " +
                    handler.getAnnotationType().getSimpleName());
        }
    }

    public List<ComponentAnnotationHandler> getComponentHandlers() {
        return new ArrayList<>(componentHandlers);
    }

    public List<ParameterAnnotationHandler> getParameterHandlers() {
        return new ArrayList<>(parameterHandlers);
    }

    public void clear() {
        componentHandlers.clear();
        parameterHandlers.clear();
    }
}