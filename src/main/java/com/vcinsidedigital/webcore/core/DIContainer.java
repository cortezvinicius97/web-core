package com.vcinsidedigital.webcore.core;


import com.vcinsidedigital.webcore.annotations.*;
import com.vcinsidedigital.webcore.extensibility.AnnotationHandlerRegistry;
import com.vcinsidedigital.webcore.extensibility.ComponentAnnotationHandler;
import java.lang.reflect.*;
import java.util.*;

public class DIContainer {
    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Map<Class<?>, Class<?>> bindings = new HashMap<>();

    public void register(Class<?> clazz) {
        if (shouldRegister(clazz)) {
            getInstance(clazz);
        }
    }

    private boolean shouldRegister(Class<?> clazz) {
        // Check built-in annotations
        if (clazz.isAnnotationPresent(Component.class) ||
                clazz.isAnnotationPresent(Service.class) ||
                clazz.isAnnotationPresent(Repository.class) ||
                clazz.isAnnotationPresent(RestController.class) ||
                clazz.isAnnotationPresent(Controller.class)) {
            return true;
        }

        // Check custom component handlers
        for (ComponentAnnotationHandler handler : AnnotationHandlerRegistry.getInstance().getComponentHandlers()) {
            if (handler.isComponent(clazz)) {
                return true;
            }
        }

        return false;
    }

    public <T> T getInstance(Class<T> clazz) {
        if (instances.containsKey(clazz)) {
            return (T) instances.get(clazz);
        }

        try {
            T instance = createInstance(clazz);
            instances.put(clazz, instance);
            injectFields(instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
        }
    }

    private <T> T createInstance(Class<T> clazz) throws Exception {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class) ||
                    constructor.getParameterCount() > 0) {
                constructor.setAccessible(true);
                Parameter[] params = constructor.getParameters();
                Object[] args = new Object[params.length];

                for (int i = 0; i < params.length; i++) {
                    args[i] = getInstance(params[i].getType());
                }

                return (T) constructor.newInstance(args);
            }
        }

        return clazz.getDeclaredConstructor().newInstance();
    }

    private void injectFields(Object instance) throws Exception {
        Class<?> clazz = instance.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                Object dependency = getInstance(field.getType());
                field.set(instance, dependency);
            }
        }
    }

    public Collection<Object> getAllInstances() {
        return instances.values();
    }
}