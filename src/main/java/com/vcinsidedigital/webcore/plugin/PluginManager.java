package com.vcinsidedigital.webcore.plugin;

import java.util.*;

import com.vcinsidedigital.webcore.routing.Router;

public class PluginManager {
    private final List<PluginInterface> plugins = new ArrayList<>();
    private final Map<String, PluginInterface> pluginIds = new HashMap<>();

    public void registerPlugin(PluginInterface plugin) {
        String pluginId = plugin.getId();

        // Check for duplicate ID
        if (pluginIds.containsKey(pluginId)) {
            PluginInterface existingPlugin = pluginIds.get(pluginId);
            throw new DuplicatePluginException(
                    pluginId,
                    existingPlugin.getName() + " v" + existingPlugin.getVersion(),
                    plugin.getName() + " v" + plugin.getVersion()
            );
        }

        // Check for duplicate instance (fallback)
        if (plugins.contains(plugin)) {
            System.out.println("  ⏭️  Plugin instance already registered: " + plugin.getName());
            return;
        }

        plugins.add(plugin);
        pluginIds.put(pluginId, plugin);
        System.out.println("  ✅ Plugin registered: " + plugin.getName() + " v" + plugin.getVersion() + " (ID: " + pluginId + ")");
    }

    public void registerPlugin(Class<? extends PluginInterface> pluginClass) {
        try {
            PluginInterface plugin = pluginClass.getDeclaredConstructor().newInstance();
            registerPlugin(plugin);
        } catch (DuplicatePluginException e) {
            // Re-throw duplicate exceptions
            throw e;
        } catch (Exception e) {
            System.err.println("  ❌ Failed to register plugin: " + pluginClass.getName());
            e.printStackTrace();
        }
    }

    public boolean isPluginRegistered(Class<? extends PluginInterface> pluginClass) {
        return plugins.stream()
                .anyMatch(plugin -> plugin.getClass().equals(pluginClass));
    }

    public boolean isPluginIdRegistered(String pluginId) {
        return pluginIds.containsKey(pluginId);
    }

    public Optional<PluginInterface> getPluginById(String pluginId) {
        return Optional.ofNullable(pluginIds.get(pluginId));
    }

    public boolean hasServerInitializer() {
        return plugins.stream().anyMatch(PluginInterface::isInitializeServer);
    }

    public void loadPlugins(com.vcinsidedigital.webcore.WebServerApplication application) {
        System.out.println("\n🔌 Loading plugins:");
        for (PluginInterface plugin : plugins) {
            try {
                plugin.onLoad(application);
                System.out.println("  ├─ Loaded: " + plugin.getName());
            } catch (Exception e) {
                System.err.println("  ├─ ❌ Error loading plugin: " + plugin.getName());
                e.printStackTrace();
            }
        }
    }

    public void startPlugins(com.vcinsidedigital.webcore.WebServerApplication application) {
        System.out.println("\n🚀 Starting plugins:");
        for (PluginInterface plugin : plugins) {
            try {
                plugin.onStart(application);
                System.out.println("  ├─ Started: " + plugin.getName());
            } catch (Exception e) {
                System.err.println("  ├─ ❌ Error starting plugin: " + plugin.getName());
                e.printStackTrace();
            }
        }
    }

    public void initializeServer(Router router, String[] args, String hostname, int port) {
        System.out.println("\n⚙️  Initializing server with plugins:");
        for (PluginInterface plugin : plugins) {
            if (plugin.isInitializeServer()) {
                try {
                    plugin.onServerInit(router, args, hostname, port);
                    System.out.println("  ├─ Server initialized by: " + plugin.getName());
                } catch (Exception e) {
                    System.err.println("  ├─ ❌ Error initializing server with plugin: " + plugin.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    public List<PluginInterface> getPlugins() {
        return new ArrayList<>(plugins);
    }

    public List<String> getPluginPackages() {
        List<String> packages = new ArrayList<>();
        for (PluginInterface plugin : plugins) {
            packages.add(plugin.getBasePackage());
        }
        return packages;
    }

    public <T extends PluginInterface> Optional<T> getPlugin(Class<T> pluginClass) {
        return plugins.stream()
                .filter(pluginClass::isInstance)
                .map(pluginClass::cast)
                .findFirst();
    }
}
