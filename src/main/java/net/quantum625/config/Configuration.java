package net.quantum625.config;

import net.quantum625.config.util.FileType;
import net.quantum625.config.util.exceptions.ConfigAlreadyRegisteredException;
import net.quantum625.config.util.exceptions.InvalidFileFormatExecption;
import net.quantum625.config.util.exceptions.InvalidNodeException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import java.util.logging.Logger;

public final class Configuration extends RawConfiguration {

    private final boolean logging = true;

    private final String filepath;
    private final String filename;

    private HoconConfigurationLoader loader;
    private CommentedConfigurationNode root;



    @ApiStatus.Internal
    private Configuration(JavaPlugin plugin, String registeredPath, Path filePath) throws ConfigAlreadyRegisteredException {
        this.plugin = plugin;

        if (registeredPath == null || registeredPath.equals("")) {
            this.path = plugin.getName().toLowerCase();
        }
        else {
            this.path = plugin.getName().toLowerCase() + "." + registeredPath;
        }
        this.filename = filePath.toFile().getName();
        this.filepath = filePath.toFile().getPath().replace(plugin.getDataFolder().getPath()+"/", "");

        this.loader = HoconConfigurationLoader.builder()
                .path(Paths.get(plugin.getDataFolder().getAbsolutePath()+"/"+filename))
                .build();

        this.logger = plugin.getLogger();
        
        
        ConfigurationManager.addConfiguration(this);

        update();
    }


    /**
     * Updates the config file, so that new added configs are also part of the config
     * @Warning: This will override all settings changed
     */
    @Override
    protected void update() {
        reload();
        plugin.saveResource(filepath, true);

        CommentedConfigurationNode defaultConfig = null;

        try {
            HoconConfigurationLoader defaultLoader = HoconConfigurationLoader.builder()
                    .path(Paths.get(plugin.getDataFolder().getAbsolutePath()+"/"+filename))
                    .build();


            defaultConfig = defaultLoader.load();

        } catch (final ConfigurateException e) {
            logger.severe("[QC] Error while loading default config file from jar class:");
            new RuntimeException(e);
        }

        if (defaultConfig == null) {
            logger.severe("[QC] Error while loading default config file from jar class");
            new RuntimeException("Default config is null!");
        }

        root = root.mergeFrom(defaultConfig);


        try {
            loader.save(root);
        } catch (final ConfigurateException e) {
            logger.severe("[QC] Unable to update configuration file " + filename + ":");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
        }

        logger.info("[QC] Successfully updated config file " + filename);
    }



    /**
     * Creates a configuration instance for a plugin's main config
     * @param plugin The Plugin, the config belongs to
     * @param file The file, which is loaded
     * @throws ConfigAlreadyRegisteredException
     */
    public static Configuration createMain(JavaPlugin plugin, File file) throws ConfigAlreadyRegisteredException {
        return new Configuration(plugin, "", Paths.get(file.getAbsolutePath()));
    }


    /**
     * Creates a configuration instance for a config file
     * @param plugin The Plugin, the config belongs to
     * @param registeredPath The Path on which the config is registered in the ConfigurationManager
     * @param file The file, which is loaded
     * @throws ConfigAlreadyRegisteredException
     */
    public static Configuration create(JavaPlugin plugin, String registeredPath, File file) throws ConfigAlreadyRegisteredException {
        return new Configuration(plugin, registeredPath, Paths.get(file.getAbsolutePath()));
    }



    /**
     * Creates a configuration instance for a plugin's main config
     * @param plugin The Plugin, the config belongs to
     * @param filename The name of the config file (DO NOT add the Path!)
     * @throws ConfigAlreadyRegisteredException
     */
    public static Configuration createMain(JavaPlugin plugin, String filename) throws ConfigAlreadyRegisteredException {

        if (filename.endsWith(".conf")) {
            filename = filename.substring(0, filename.length()-5);
        }

        return new Configuration(plugin, "", Paths.get(plugin.getDataFolder() + "/" + filename + ".conf"));
    }


    /**
     * Creates a configuration instance for a config file
     * @param plugin The Plugin, the config belongs to
     * @param registeredPath The Path on which the config is registered in the ConfigurationManager
     * @param filename The name of the config file (Do not add the path!)
     * @throws ConfigAlreadyRegisteredException
     */
    public static Configuration create(JavaPlugin plugin, String registeredPath, String filename) throws ConfigAlreadyRegisteredException {


        if (filename.endsWith(".conf")) {
            filename = filename.substring(0, filename.length()-5);
        }

        return new Configuration(plugin, registeredPath, Paths.get(plugin.getDataFolder() + "/" + filename + ".conf"));
    }

    /**
     * Creates a configuration instance for a plugin's main config
     * @param plugin The Plugin, the config belongs to
     * @param filepath The Path of the file's path (Will be appended to the Plugin's data folder, Do not add the datafolder at the start or the filename at the end!)
     * @param filename The name of the config file (Without the .conf)
     * @throws ConfigAlreadyRegisteredException
     */
    public static Configuration createMain(JavaPlugin plugin, String filepath, String filename) throws ConfigAlreadyRegisteredException {


        if (filename.endsWith(".conf")) {
            filename = filename.substring(0, filename.length()-5);
        }

        if (filepath.endsWith("/")) {
            filepath = filepath.substring(0, filepath.length()-1);
        }

        if (filepath.startsWith("/")) {
            filepath = filepath.substring(1);
        }

        return new Configuration(plugin, "", Paths.get(plugin.getDataFolder() + "/" + filepath + "/" + filename + ".conf"));
    }


    /**
     * Creates a configuration instance for a config file
     * @param plugin The Plugin, the config belongs to
     * @param registeredPath The Path on which the config is registered in the ConfigurationManager
     * @param filepath The Path of the file's path (Will be appended to the Plugin's data folder, Do not add the datafolder at the start or the filename at the end!)
     * @param filename The name of the config file (Without the .conf)
     * @throws ConfigAlreadyRegisteredException
     */
    public static Configuration create(JavaPlugin plugin, String registeredPath, String filepath, String filename) throws ConfigAlreadyRegisteredException {


        if (filename.endsWith(".conf")) {
            filename = filename.substring(0, filename.length()-5);
        }

        if (filepath.endsWith("/")) {
            filepath = filepath.substring(0, filepath.length()-1);
        }

        if (filepath.startsWith("/")) {
            filepath = filepath.substring(1);
        }

        return new Configuration(plugin, registeredPath, Paths.get(plugin.getDataFolder() + "/" + filepath + "/" + filename + ".conf"));
    }






    /**
     * Reloads the configuration file
     * @Warning: This will override all settings changed
     */
    public void reload() {


        this.logger = plugin.getLogger();

        try {
            root = loader.load();

            if (root == null) {
                logger.severe("[QC] Failed to load configuration " + filename + ", root configuration is null");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }

            try {
                ingameEdit = getBoolean("allowIngameEdit");
            }
            catch (InvalidNodeException e) {
                set("allowIngameEdit", ingameEdit);
            }

        } catch (final ConfigurateException e) {
            logger.severe("[QC] Could not load configuration " + filename + ": Invalid Syntax");
            throw new RuntimeException(e);
        }

        logger.info("[QC] Successfully loaded configuration file " + filename + " on root path " + path);
    }


    /**
     * Saves the configuration file
     * @Warning: This will override the existing file!
     */
    public void save() {
        try {
            loader.save(root);
        } catch (final ConfigurateException e) {
            logger.severe("[QC] Unable to save configuration file " + filename + ":\n" + e.getMessage());
        }
    }


    /**
     * Enables the /config edit command for this file
     * (Disabled by default)
     */
    public void enableIngameChange() {
        ingameEdit = true;
        set("allowIngameEdit", ingameEdit, "Whether values in this config files are changable via /config set ");
    }


    /**
     * Disables the /config edit command for this file
     * (Disabled by default)
     */
    public void disableIngameChange() {
        ingameEdit = false;
        set("allowIngameEdit", ingameEdit, "Whether values in this config files are changable via /config set ");

    }


    /**
     * @return Whether, using /config edit is allowed for this config file
     */
    public boolean ingameChangeEnabled() {
        return ingameEdit;
    }



    /**
     * Should only be used to access Configurate features, that can't be accessed otherwise!
     * @return the root configuration node
     */
    @Deprecated
    public CommentedConfigurationNode getRootConfigurationNode() {
        return root;
    }



    /**
     * @param path The path of the requested node
     * @return the given node
     */
    public ConfigurationNode get(String path) throws InvalidNodeException {
        ConfigurationNode node = root.node(path.split("\\."));
        if (node.isNull()) throw new InvalidNodeException("[QC] Node " + path + " in configuration " + this.path + " does not exist!");
        return node;
    }

    public boolean getBoolean(String path) throws InvalidNodeException {
        return get(path).getBoolean();
    }

    public @Nullable int getInt(String path) throws InvalidNodeException {
        return get(path).getInt();
    }

    public @Nullable double getDouble(String path) throws InvalidNodeException {
        return get(path).getDouble();
    }

    public @Nullable String getString(String path) throws InvalidNodeException {
        return get(path).getString();
    }

    public <E> @Nullable List<E> getList(String path, Class<E> type) throws InvalidNodeException, SerializationException {
        try {
            return get(path).getList(type);
        }
        catch (SerializationException e) {
            throw new SerializationException("Unmatching data types found while serializing node " + path);
        }
    }


    /**
     * Same as getBoolean(), but it sends an error to the console instead of throwing an exception
     */
    public @Nullable Boolean getFinalBoolean(String path) {
        try {
            return getBoolean(path);
        }
        catch (InvalidNodeException e) {
            logger.severe("[QC] InvalidNodeException: Request for non-existent node: " + path);
        }
        return null;
    }

    /**
     * Same as getInt(), but it sends an error to the console instead of throwing an exception
     */
    public @Nullable Integer getFinalInt(String path)  {
        try {
            return getInt(path);
        }
        catch (InvalidNodeException e) {
            logger.severe("[QC] InvalidNodeException: Request for non-existent node: " + path);
        }
        return null;
    }

    /**
     * Same as getDouble(), but it sends an error to the console instead of throwing an exception
     */
    public @Nullable Double getFinalDouble(String path) {
        try {
            return getDouble(path);
        }
        catch (InvalidNodeException e) {
            logger.severe("[QC] InvalidNodeException: Request for non-existent node: " + path);
        }
        return null;
    }

    /**
     * Same as getString(), but it sends an error to the console instead of throwing an exception
     */
    public @Nullable String getFinalString(String path) {
        try {
            return getString(path);
        }
        catch (InvalidNodeException e) {
            logger.severe("[QC] InvalidNodeException: Request for non-existent node: " + path);
        }
        return null;
    }

    /**
     * Same as getList(), but it sends an error to the console instead of throwing an exception
     */
    public <E> @Nullable List<E> getFinalList(String path, Class<E> type) {
        try {
            return get(path).getList(type);
        }
        catch (SerializationException e) {
            logger.severe("[QC] SerializationException: Unmatching data types found while serializing node " + path);
        }
        catch (InvalidNodeException e) {
            logger.severe("[QC] InvalidNodeException: Request for non-existent node: " + path);
        }
        return null;
    }





    /**
     * Changes the value on a given path or creates a new value
     * @param path The path of the node
     * @param value The new value
     */
    public void set(@NotNull String path, Object value) {
        try {
            root.node(path.split("\\.")).set(value.getClass(), value);
        }
        catch (SerializationException e) {
            logger.severe("[QC] SerializationException: " + e.getMessage());
        }
    }


    /**
     * Changes the value on a given path or creates a new value
     * @param path The path of the node
     * @param value The new value
     * @param comment A comment for the setting
     */
    public void set(@NotNull String path, Object value, String comment) {
        try {
            root.node(path.split("\\.")).act(n -> {
                n.comment(comment);
                n.set(value);
            });
        }
        catch (SerializationException e) {
            logger.severe("[QC] SerializationException: " + e.getMessage());
        }
    }

    public boolean has(@NotNull String path) {
        return !root.node(path.split("\\.")).isNull();
    }

    public String filename() {
        return filename + ".conf";
    }

    /**
     * @return The plugin, the configuration is registered by
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }
}