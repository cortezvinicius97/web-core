package com.externalplugin.annotations;

import com.externalplugin.annotations.handlers.ApiControllerHandler;
import com.externalplugin.annotations.handlers.CookieParameterHandler;
import com.externalplugin.annotations.handlers.HeaderParameterHandler;
import com.vcinsidedigital.webcore.WebServerApplication;
import com.vcinsidedigital.webcore.annotations.Plugin;
import com.vcinsidedigital.webcore.extensibility.AnnotationHandlerRegistry;
import com.vcinsidedigital.webcore.plugin.AbstractPlugin;

@Plugin
public class PluginAnnotations extends AbstractPlugin
{
    @Override
    public void onLoad(WebServerApplication application) {
        System.out.println("    ├─ Registering custom annotation handlers...");

        AnnotationHandlerRegistry registry = AnnotationHandlerRegistry.getInstance();

        // Register component handler
        registry.registerComponentHandler(new ApiControllerHandler());

        // Register parameter handlers
        registry.registerParameterHandler(new HeaderParameterHandler());
        registry.registerParameterHandler(new CookieParameterHandler());
    }

    @Override
    public void onStart(WebServerApplication application) {
        System.out.println("    ├─ Annotations started");
    }

    @Override
    public String getId() {
        return "com.externalplugin.annotations";
    }

    @Override
    public String getName() {
        return "Custom Annotations";
    }

    @Override
    public String getVersion() {
        return super.getVersion();
    }
}
