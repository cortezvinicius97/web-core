package com.vcinsidedigital.webcore.plugin;

import java.util.*;

import com.vcinsidedigital.webcore.routing.Router;
import com.vcinsidedigital.webcore.server.ServerConfiguration;
import com.vcinsidedigital.webcore.server.ServerCustomizer;

import com.vcinsidedigital.webcore.routing.Router;
import com.vcinsidedigital.webcore.server.ServerConfiguration;
import com.vcinsidedigital.webcore.server.ServerCustomizer;
import java.util.*;

public class PluginManager {
    private final List<PluginInterface> plugins = new ArrayList<>();
    private final Map<String, PluginInterface> pluginIds = new HashMap<>();
    private final Set<String> registeredPluginPackages = new HashSet<>();
    private final Set<String> failedPluginPackages = new HashSet<>();

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
        registeredPluginPackages.add(plugin.getBasePackage());
        System.out.println("  ✅ Plugin registered: " + plugin.getName() + " v" + plugin.getVersion() + " (ID: " + pluginId + ")");
    }

    public void registerPlugin(Class<? extends PluginInterface> pluginClass) {
        try {
            PluginInterface plugin = pluginClass.getDeclaredConstructor().newInstance();

            // Check for duplicate ID BEFORE registering
            String pluginId = plugin.getId();
            if (pluginIds.containsKey(pluginId)) {
                PluginInterface existingPlugin = pluginIds.get(pluginId);
                throw new DuplicatePluginException(
                        pluginId,
                        existingPlugin.getName() + " v" + existingPlugin.getVersion(),
                        plugin.getName() + " v" + plugin.getVersion()
                );
            }

            // Now actually register
            registerPlugin(plugin);

        } catch (DuplicatePluginException e) {
            // Re-throw duplicate exceptions
            throw e;
        } catch (Exception e) {
            System.err.println("  ❌ Failed to instantiate plugin: " + pluginClass.getName());
            throw new RuntimeException("Failed to instantiate plugin: " + pluginClass.getName(), e);
        }
    }

    /**
     * Mark a package as failed (plugin registration failed)
     * Components from this package should not be registered
     */
    public void markPackageAsFailed(String packageName) {
        failedPluginPackages.add(packageName);
    }

    /**
     * Check if a package is from a failed plugin
     */
    public boolean isPackageFailed(String packageName) {
        // Check exact match or if package is sub-package of failed plugin
        for (String failedPackage : failedPluginPackages) {
            if (packageName.equals(failedPackage) || packageName.startsWith(failedPackage + ".")) {
                return true;
            }
        }
        return false;
    }

    public boolean isPluginRegistered(Class<? extends PluginInterface> pluginClass) {
        return plugins.stream()
                .anyMatch(plugin -> plugin.getClass().equals(pluginClass));
    }

    public boolean isPluginIdRegistered(String pluginId) {
        return pluginIds.containsKey(pluginId);
    }

    public boolean isPluginPackageRegistered(String packageName) {
        return registeredPluginPackages.contains(packageName);
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

                // Register server customizations
                ServerConfiguration config = plugin.getServerConfiguration();
                if (config != null) {
                    ServerCustomizer customizer = ServerCustomizer.getInstance();

                    try {
                        customizer.registerPortCustomization(config, plugin.getName());
                    } catch (IllegalStateException e) {
                        System.err.println("  ├─ ❌ " + e.getMessage());
                    }

                    try {
                        customizer.registerHostCustomization(config, plugin.getName());
                    } catch (IllegalStateException e) {
                        System.err.println("  ├─ ❌ " + e.getMessage());
                    }

                    try {
                        customizer.registerRequestCustomization(config, plugin.getName());
                    } catch (IllegalStateException e) {
                        System.err.println("  ├─ ❌ " + e.getMessage());
                    }

                    try {
                        customizer.registerResponseCustomization(config, plugin.getName());
                    } catch (IllegalStateException e) {
                        System.err.println("  ├─ ❌ " + e.getMessage());
                    }
                }

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