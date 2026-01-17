package com.externalplugin.annotations.handlers;

import com.externalplugin.annotations.annotation.Header;
import com.vcinsidedigital.webcore.extensibility.ParameterAnnotationHandler;
import com.vcinsidedigital.webcore.extensibility.ParameterContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

public class HeaderParameterHandler implements ParameterAnnotationHandler {
    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return Header.class;
    }

    @Override
    public boolean canHandle(Parameter parameter) {
        return parameter.isAnnotationPresent(Header.class);
    }

    @Override
    public Object resolveParameter(Parameter parameter, ParameterContext context) throws Exception {
        Header header = parameter.getAnnotation(Header.class);
        String headerName = header.value();
        String headerValue = context.getHeader(headerName);

        if (headerValue == null && header.required()) {
            throw new IllegalArgumentException("Required header '" + headerName + "' is missing");
        }

        return convertValue(headerValue, parameter.getType());
    }

    private Object convertValue(String value, Class<?> type) {
        if (value == null) return null;
        if (type == String.class) return value;
        if (type == Integer.class || type == int.class) return Integer.parseInt(value);
        if (type == Long.class || type == long.class) return Long.parseLong(value);
        if (type == Boolean.class || type == boolean.class) return Boolean.parseBoolean(value);
        return value;
    }
}
