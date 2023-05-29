package net.quantum625.config;

import net.quantum625.config.util.exceptions.ConfigAlreadyRegisteredException;
import net.quantum625.config.util.exceptions.InvalidNodeException;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;

public final class ConfigurationManager  {

    /**
     * A list of all registered Configuration instances
     */
    private static List<Configuration> rootNodes = new ArrayList<Configuration>();


    /**
     * Register a configuration instance to the manager
     * @param configuration The configuration instance to be added
     * @throws ConfigAlreadyRegisteredException when there is already a config registered at the same path
     */
    public static void addConfiguration(Configuration configuration) throws ConfigAlreadyRegisteredException {
        for (Configuration conf : rootNodes) {
            if (configuration.getPath().equalsIgnoreCase(conf.getPath())) throw new ConfigAlreadyRegisteredException("There is already a config registered with the path " + conf.getPath());
        }
        rootNodes.add(configuration);
    }


    /**
     * @return a root configuration node
     * @param path the path of the root configuration node, WITH the plugin's name
     * @throws InvalidNodeException
     */
    public static @Nullable Configuration getRootConfiguration(@NotNull String path) throws InvalidNodeException {
        for (Configuration c : rootNodes) {
            if (c.getPath().equalsIgnoreCase(path)) {
                return c;
            }
        }

        throw new InvalidNodeException("No root node found with the path " + path);
    }


    /**
     * @return the main root configuration of a plugin (each plugin can only have a maximum of one)
     * @param plugin the plugin, whose configuration is accessed
     * @throws InvalidNodeException
     */
    public static @Nullable Configuration getRootConfiguration(@NotNull JavaPlugin plugin) throws InvalidNodeException {
        return getRootConfiguration(plugin.getName());
    }


    /**
     * @return a root configuration node
     * @param path the path of the root configuration node, but WITHOUT the plugin's name at the front
     * @param plugin the plugin, whose configuration is accessed
     * @throws InvalidNodeException
     */
    public static @Nullable Configuration getRootConfiguration(@NotNull JavaPlugin plugin, @NotNull String path) throws InvalidNodeException {
        return getRootConfiguration(plugin.getName() + "." + path);
    }



    /**
     * @return a configuration node with a given path
     * @param nodePath the full path of the node (root configuration path + node path)
     * @throws InvalidNodeException
     */
    public static @Nullable ConfigurationNode getNode(@NotNull String rootPath, @NotNull String nodePath) throws InvalidNodeException {
        Configuration c = getRootConfiguration(rootPath);
        if (c == null) throw new InvalidNodeException("No root node found with given path");
        return c.get(nodePath);

    }

    /**
     * Saves all loaded configurations
     */
    public static void saveAll() {
        for (Configuration c : rootNodes) {
            c.save();
        }
    }


}
