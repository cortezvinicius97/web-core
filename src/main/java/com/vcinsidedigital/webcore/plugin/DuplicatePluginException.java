package com.vcinsidedigital.webcore.plugin;

public class DuplicatePluginException extends RuntimeException {

    private final String pluginId;
    private final String existingPluginName;
    private final String newPluginName;

    public DuplicatePluginException(String pluginId, String existingPluginName, String newPluginName) {
        super(String.format(
                "Plugin ID conflict detected!\n" +
                        "  Plugin ID: '%s'\n" +
                        "  Already registered: %s\n" +
                        "  Attempted to register: %s\n" +
                        "  Each plugin must have a unique ID.",
                pluginId, existingPluginName, newPluginName
        ));
        this.pluginId = pluginId;
        this.existingPluginName = existingPluginName;
        this.newPluginName = newPluginName;
    }

    public String getPluginId() {
        return pluginId;
    }

    public String getExistingPluginName() {
        return existingPluginName;
    }

    public String getNewPluginName() {
        return newPluginName;
    }
}
