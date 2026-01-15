package com.vcinsidedigital.webcore.routing;

import com.vcinsidedigital.webcore.annotations.Middleware;
import com.vcinsidedigital.webcore.http.*;
import com.vcinsidedigital.webcore.middleware.MiddlewareHandler;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.*;

public class Route {
    private final String method;
    private final String path;
    private final Pattern pattern;
    private final List<String> pathVariables;
    private final Object controller;
    private final Method handlerMethod;
    private final List<Class<? extends MiddlewareHandler>> classMiddlewares;
    private final List<Class<? extends MiddlewareHandler>> methodMiddlewares;

    public Route(String method, String path, Object controller, Method handlerMethod) {
        this.method = method;
        this.path = path;
        this.controller = controller;
        this.handlerMethod = handlerMethod;
        this.pathVariables = new ArrayList<>();
        this.pattern = compilePattern(path);
        this.classMiddlewares = extractClassMiddlewares(controller);
        this.methodMiddlewares = extractMethodMiddlewares(handlerMethod);
    }

    private List<Class<? extends MiddlewareHandler>> extractClassMiddlewares(Object controller) {
        List<Class<? extends MiddlewareHandler>> middlewares = new ArrayList<>();
        Class<?> clazz = controller.getClass();

        if (clazz.isAnnotationPresent(Middleware.class)) {
            Middleware annotation = clazz.getAnnotation(Middleware.class);
            middlewares.addAll(Arrays.asList(annotation.value()));
        }

        return middlewares;
    }

    private List<Class<? extends MiddlewareHandler>> extractMethodMiddlewares(Method method) {
        List<Class<? extends MiddlewareHandler>> middlewares = new ArrayList<>();

        if (method.isAnnotationPresent(Middleware.class)) {
            Middleware annotation = method.getAnnotation(Middleware.class);
            middlewares.addAll(Arrays.asList(annotation.value()));
        }

        return middlewares;
    }

    private Pattern compilePattern(String path) {
        Matcher matcher = Pattern.compile("\\{([^}]+)\\}").matcher(path);
        String regex = path;

        while (matcher.find()) {
            pathVariables.add(matcher.group(1));
            regex = regex.replace("{" + matcher.group(1) + "}", "([^/]+)");
        }

        return Pattern.compile("^" + regex + "$");
    }

    public boolean matches(String method, String path) {
        return this.method.equalsIgnoreCase(method) && pattern.matcher(path).matches();
    }

    public Map<String, String> extractPathParams(String path) {
        Map<String, String> params = new HashMap<>();
        Matcher matcher = pattern.matcher(path);

        if (matcher.matches()) {
            for (int i = 0; i < pathVariables.size(); i++) {
                params.put(pathVariables.get(i), matcher.group(i + 1));
            }
        }

        return params;
    }

    public Object getController() { return controller; }
    public Method getHandlerMethod() { return handlerMethod; }
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public Pattern getPattern() { return pattern; }
    public List<Class<? extends MiddlewareHandler>> getClassMiddlewares() { return classMiddlewares; }
    public List<Class<? extends MiddlewareHandler>> getMethodMiddlewares() { return methodMiddlewares; }
}
