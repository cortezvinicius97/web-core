package com.vcinsidedigital.webcore.routing;

import com.vcinsidedigital.webcore.annotations.*;
import com.vcinsidedigital.webcore.http.*;
import com.google.gson.Gson;
import com.vcinsidedigital.webcore.middleware.MiddlewareHandler;

import java.lang.reflect.*;
import java.util.*;

public class Router {
    private final List<Route> routes = new ArrayList<>();
    private final Gson gson = new Gson();

    public void registerController(Object controller) {
        Class<?> clazz = controller.getClass();
        String basePath = "";

        if (clazz.isAnnotationPresent(RestController.class)) {
            basePath = clazz.getAnnotation(RestController.class).path();
        } else if (clazz.isAnnotationPresent(Controller.class)) {
            basePath = clazz.getAnnotation(Controller.class).path();
        }

        for (Method method : clazz.getDeclaredMethods()) {
            registerRoute(controller, method, basePath, Get.class, "GET");
            registerRoute(controller, method, basePath, Post.class, "POST");
            registerRoute(controller, method, basePath, Put.class, "PUT");
            registerRoute(controller, method, basePath, Delete.class, "DELETE");
            registerRoute(controller, method, basePath, Patch.class, "PATCH");
            registerRoute(controller, method, basePath, Options.class, "OPTIONS");
        }
    }

    private void registerRoute(Object controller, Method method, String basePath,
                               Class<? extends java.lang.annotation.Annotation> annotation, String httpMethod) {
        if (method.isAnnotationPresent(annotation)) {
            try {
                String path = (String) annotation.getMethod("value").invoke(method.getAnnotation(annotation));
                String fullPath = normalizePath(basePath + path);
                routes.add(new Route(httpMethod, fullPath, controller, method));
                System.out.println("  [ROUTE] " + httpMethod + " " + fullPath + " -> " +
                        controller.getClass().getSimpleName() + "." + method.getName() + "()");
            } catch (Exception e) {
                throw new RuntimeException("Failed to register route", e);
            }
        }
    }

    private String normalizePath(String path) {
        if (!path.startsWith("/")) path = "/" + path;
        return path.replaceAll("/+", "/");
    }

    public HttpResponse handleRequest(HttpRequest request) {
        for (Route route : routes) {
            if (route.matches(request.getMethod(), request.getPath())) {
                try {
                    Map<String, String> pathParams = route.extractPathParams(request.getPath());
                    request = new HttpRequest(request.getMethod(), request.getPath(), pathParams,
                            request.getQueryParams(), request.getBody(), request.getHeaders());

                    // Execute class-level middlewares
                    HttpResponse middlewareResponse = executeMiddlewares(route.getClassMiddlewares(), request);
                    if (middlewareResponse != null) {
                        return middlewareResponse;
                    }

                    // Execute method-level middlewares
                    middlewareResponse = executeMiddlewares(route.getMethodMiddlewares(), request);
                    if (middlewareResponse != null) {
                        return middlewareResponse;
                    }

                    // Execute controller method
                    Object result = invokeHandler(route, request);

                    // Check if result is already an HttpResponse
                    if (result instanceof HttpResponse) {
                        HttpResponse response = (HttpResponse) result;

                        // Apply @ResponseStatus if present and status not already set
                        Method handlerMethod = route.getHandlerMethod();
                        if (handlerMethod.isAnnotationPresent(ResponseStatus.class) && response.getStatusCode() == 200) {
                            ResponseStatus responseStatus = handlerMethod.getAnnotation(ResponseStatus.class);
                            response.status(responseStatus.value().getCode());
                        }

                        return response;
                    }

                    // Otherwise, create response from result
                    HttpResponse response = createResponse(result);

                    // Apply @ResponseStatus annotation
                    Method handlerMethod = route.getHandlerMethod();
                    if (handlerMethod.isAnnotationPresent(ResponseStatus.class)) {
                        ResponseStatus responseStatus = handlerMethod.getAnnotation(ResponseStatus.class);
                        response.status(responseStatus.value().getCode());
                    }

                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                    return new HttpResponse()
                            .status(500)
                            .body("{\"error\": \"" + e.getMessage() + "\"}");
                }
            }
        }

        // Handle OPTIONS requests for CORS preflight (catch-all for routes with middlewares)
        if ("OPTIONS".equals(request.getMethod())) {
            // Try to find matching route with any method to execute middlewares
            for (Route route : routes) {
                String tempPath = request.getPath();
                // Check if path pattern matches (ignoring method)
                if (route.getPattern().matcher(tempPath).matches()) {
                    try {
                        // Execute middlewares for CORS handling
                        HttpResponse middlewareResponse = executeMiddlewares(route.getClassMiddlewares(), request);
                        if (middlewareResponse != null) {
                            return middlewareResponse;
                        }
                    } catch (Exception e) {
                        // Continue to next route
                    }
                }
            }
        }

        return new HttpResponse()
                .status(404)
                .body("{\"error\": \"Not Found\"}");
    }

    private HttpResponse executeMiddlewares(List<Class<? extends MiddlewareHandler>> middlewareClasses, HttpRequest request) {
        for (Class<? extends MiddlewareHandler> middlewareClass : middlewareClasses) {
            try {
                MiddlewareHandler middleware = middlewareClass.getDeclaredConstructor().newInstance();
                HttpResponse response = middleware.handle(request);

                if (response != null) {
                    return response; // Short-circuit if middleware returns a response
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new HttpResponse()
                        .status(500)
                        .body("{\"error\": \"Middleware error: " + e.getMessage() + "\"}");
            }
        }
        return null; // Continue to next middleware/controller
    }

    private Object invokeHandler(Route route, HttpRequest request) throws Exception {
        Method method = route.getHandlerMethod();
        method.setAccessible(true);
        Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            if (params[i].isAnnotationPresent(Path.class)) {
                String paramName = params[i].getAnnotation(Path.class).value();
                String value = request.getPathParams().get(paramName);
                args[i] = convertParameter(value, params[i].getType());
            } else if (params[i].isAnnotationPresent(Body.class)) {
                args[i] = gson.fromJson(request.getBody(), params[i].getType());
            } else if (params[i].isAnnotationPresent(Query.class)) {
                String paramName = params[i].getAnnotation(Query.class).value();
                String value = request.getQueryParams().get(paramName);
                args[i] = convertParameter(value, params[i].getType());
            }
        }

        return method.invoke(route.getController(), args);
    }

    private Object convertParameter(String value, Class<?> type) {
        if (value == null) return null;
        if (type == String.class) return value;
        if (type == Long.class || type == long.class) return Long.parseLong(value);
        if (type == Integer.class || type == int.class) return Integer.parseInt(value);
        if (type == Boolean.class || type == boolean.class) return Boolean.parseBoolean(value);
        return value;
    }

    private HttpResponse createResponse(Object result) {
        // Se já é HttpResponse, retorna direto
        if (result instanceof HttpResponse) {
            return (HttpResponse) result;
        }

        // Para null, retorna 204
        if (result == null) {
            return new HttpResponse().status(204);
        }

        // Para qualquer outro tipo, serializa para JSON e coloca no body
        String jsonBody;
        if (result instanceof String) {
            jsonBody = (String) result;
        } else {
            jsonBody = gson.toJson(result);
        }

        return new HttpResponse()
                .contentType("application/json")
                .body(jsonBody);
    }

    public List<Route> getRoutes() {
        return routes;
    }
}