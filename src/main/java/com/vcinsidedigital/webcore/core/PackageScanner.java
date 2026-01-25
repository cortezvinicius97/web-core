package com.vcinsidedigital.webcore.core;

import com.vcinsidedigital.webcore.annotations.*;
import com.vcinsidedigital.webcore.extensibility.AnnotationHandlerRegistry;
import com.vcinsidedigital.webcore.extensibility.ComponentAnnotationHandler;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageScanner {

    public Set<Class<?>> scanPackage(String packageName) {
        Set<Class<?>> classes = new HashSet<>();

        try {
            String path = packageName.replace('.', '/');
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();

                if ("file".equals(protocol)) {
                    // Scanning from directory (development mode)
                    File directory = new File(URLDecoder.decode(resource.getFile(), "UTF-8"));
                    if (directory.exists()) {
                        classes.addAll(findClassesInDirectory(directory, packageName, classLoader));
                    }
                } else if ("jar".equals(protocol)) {
                    // Scanning from JAR file (production mode)
                    classes.addAll(findClassesInJar(resource, packageName, classLoader));
                }
            }
        } catch (Exception e) {
            System.err.println("Error scanning package: " + packageName);
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * Find classes in a directory (used in development)
     */
    private Set<Class<?>> findClassesInDirectory(File directory, String packageName, ClassLoader classLoader) {
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
                classes.addAll(findClassesInDirectory(file, packageName + "." + file.getName(), classLoader));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = loadClass(className, classLoader);
                if (clazz != null && isComponent(clazz)) {
                    classes.add(clazz);
                }
            }
        }

        return classes;
    }

    /**
     * Find classes in a JAR file (used in production)
     */
    private Set<Class<?>> findClassesInJar(URL resource, String packageName, ClassLoader classLoader) {
        Set<Class<?>> classes = new HashSet<>();

        try {
            // Parse JAR file path from URL
            String jarPath = resource.getPath();

            // Handle jar:file:/path/to/file.jar!/package/path format
            if (jarPath.startsWith("file:")) {
                jarPath = jarPath.substring(5);
            }

            int separatorIndex = jarPath.indexOf("!");
            if (separatorIndex != -1) {
                jarPath = jarPath.substring(0, separatorIndex);
            }

            // Decode URL encoding
            jarPath = URLDecoder.decode(jarPath, "UTF-8");

            try (JarFile jarFile = new JarFile(jarPath)) {
                String packagePath = packageName.replace('.', '/');
                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();

                    // Check if entry is in our package and is a class file
                    if (entryName.startsWith(packagePath) && entryName.endsWith(".class")) {
                        // Convert path to class name
                        String className = entryName
                                .substring(0, entryName.length() - 6) // Remove .class
                                .replace('/', '.');

                        Class<?> clazz = loadClass(className, classLoader);
                        if (clazz != null && isComponent(clazz)) {
                            classes.add(clazz);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading JAR file: " + e.getMessage());
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * Safely load a class
     */
    private Class<?> loadClass(String className, ClassLoader classLoader) {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // Ignore classes that cannot be loaded
            return null;
        }
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