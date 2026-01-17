package com.vcinsidedigital.webcore.core;

import com.vcinsidedigital.webcore.annotations.*;
import com.vcinsidedigital.webcore.extensibility.AnnotationHandlerRegistry;
import com.vcinsidedigital.webcore.extensibility.ComponentAnnotationHandler;
import java.io.File;
import java.net.URL;
import java.util.*;

public class PackageScanner {

    public Set<Class<?>> scanPackage(String packageName) {
        Set<Class<?>> classes = new HashSet<>();

        try {
            String path = packageName.replace('.', '/');
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());

                if (directory.exists()) {
                    classes.addAll(findClasses(directory, packageName));
                }
            }
        } catch (Exception e) {
            System.err.println("Error scanning package: " + packageName);
            e.printStackTrace();
        }

        return classes;
    }

    private Set<Class<?>> findClasses(File directory, String packageName) {
        Set<Class<?>> classes = new HashSet<>();

        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                try {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = Class.forName(className);

                    if (isComponent(clazz)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    // Ignorar classes que não podem ser carregadas
                } catch (NoClassDefFoundError e) {
                    // Ignorar classes com dependências faltando
                }
            }
        }

        return classes;
    }

    public boolean isComponent(Class<?> clazz) {
        // Check built-in annotations
        if (clazz.isAnnotationPresent(Component.class) ||
                clazz.isAnnotationPresent(Service.class) ||
                clazz.isAnnotationPresent(Repository.class) ||
                clazz.isAnnotationPresent(RestController.class) ||
                clazz.isAnnotationPresent(Controller.class) ||
                clazz.isAnnotationPresent(Plugin.class)) {
            return true;
        }

        // Check custom component handlers from plugins
        for (ComponentAnnotationHandler handler : AnnotationHandlerRegistry.getInstance().getComponentHandlers()) {
            if (handler.isComponent(clazz)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the component type name for display purposes
     */
    public String getComponentType(Class<?> clazz) {
        if (clazz.isAnnotationPresent(RestController.class)) return "RestController";
        if (clazz.isAnnotationPresent(Controller.class)) return "Controller";
        if (clazz.isAnnotationPresent(Service.class)) return "Service";
        if (clazz.isAnnotationPresent(Repository.class)) return "Repository";
        if (clazz.isAnnotationPresent(Component.class)) return "Component";
        if (clazz.isAnnotationPresent(Plugin.class)) return "Plugin";

        // Check custom component handlers
        for (ComponentAnnotationHandler handler : AnnotationHandlerRegistry.getInstance().getComponentHandlers()) {
            if (handler.isComponent(clazz)) {
                return handler.getAnnotationType().getSimpleName();
            }
        }

        return "Unknown";
    }
}