package dev.nanoflux.config.lang;

import dev.nanoflux.config.RawConfiguration;
import dev.nanoflux.config.util.exceptions.InvalidNodeException;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Language extends RawConfiguration {

    private final MiniMessage mm;

    private final String langID;
    private final YamlConfigurationLoader loader;
    
    private CommentedConfigurationNode root;

    public Language(@NotNull JavaPlugin plugin, @NotNull String langID) throws SerializationException {

        this.mm = MiniMessage.miniMessage();

        this.plugin = plugin;
        this.langID = langID;

        this.path = plugin.getName().toLowerCase() + ".lang." + langID ;

        this.logger = plugin.getLogger();



        this.loader = YamlConfigurationLoader.builder()
                .path(Paths.get(plugin.getDataFolder().getAbsolutePath() + "/lang/" + langID + ".yml"))
                .build();


        update();
    }


    /**
     * Updates the config file, so that new added configs are also part of the config
     * @Warning: This will override all settings changed
     */
    @Override
    protected void update() {
        reload();

        plugin.saveResource("lang/"+langID+".yml", true);

        CommentedConfigurationNode defaultConfig = null;

        try {
            YamlConfigurationLoader defaultLoader = YamlConfigurationLoader.builder()
                    .path(Paths.get(plugin.getDataFolder().getAbsolutePath() + "/lang/" + langID + ".yml"))
                    .build();


            defaultConfig = defaultLoader.load();

        } catch (final ConfigurateException e) {
            logger.severe("[QC] Error while loading default config file from jar class");
            Bukkit.getPluginManager().disablePlugin(plugin);
            throw new RuntimeException(e);
        }

        if (defaultConfig == null) {
            logger.severe("[QC] Error while loading default config file from jar class");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }

        root = root.mergeFrom(defaultConfig);


        try {
            loader.save(root);
        } catch (final ConfigurateException e) {
            logger.severe("[QC] Unable to update configuration file " + langID + ".yml:\n" + e.getMessage());
            return;
        }

        logger.info("[QC] Successfully updated config file " + langID + ".yml");

    }



    /**
     * Reloads the configuration file
     * @Warning: This will override all settings changed
     */
    @Override
    public void reload() {
        try {
            root = loader.load();

            if (root == null) {
                logger.severe("[QC] Failed to load configuration " + langID + ".yml, root configuration is null");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }

        } catch (final ConfigurateException e) {
            logger.severe("[QC] Could not load configuration " + langID + ".yml: Invalid Syntax");
            throw new RuntimeException(e);
        }

        if (testRequirements()) {
            logger.info("[QC] Successfully loaded configuration file " + langID + ".yml on root path " + path);
        }
    }



    /**
     * Saves the configuration file
     * @Warning: This will override the existing file!
     */
    public void save() {
        try {
            loader.save(root);
        } catch (final ConfigurateException e) {
            logger.severe("[QC] Unable to save configuration file " + langID + ".yml:\n" + e.getMessage());
        }
    }




    /**
     * Gives you the raw string of the given node
     * @param path The path of the requested node
     * @return Raw value of the given node
     * @throws InvalidNodeException When you give a non-existent node
     */
    public String getRaw(@NotNull String path) throws InvalidNodeException {
        ConfigurationNode node = root.node(path);
        if (node.isNull()) throw new InvalidNodeException("[QC] Node " + path + " in configuration " + this.path + " does not exist!");
        if (node.getString() == null || node.getString() == "") throw new InvalidNodeException("[QC] Node " + path + " in configuration " + this.path + " does not exist!");
        return node.getString();
    }


    public @NotNull String getPreparedString(@NotNull String path) throws InvalidNodeException {
        String result = getRaw(path);
        ConfigurationNode prefix = root.node("prefix");
        if (!prefix.isNull()) result = result.replaceAll("<prefix>", prefix.getString()+"<reset>");
        for (String key : getKeys()) {
            result = result.replaceAll("<replace: '"+key+"'>", getPreparedString(key)).replaceAll("<replace: \""+key+"\">", getPreparedString(key)).replaceAll("<replace: "+key+">", getPreparedString(key)).replaceAll("<replace:'"+key+"'>", getPreparedString(key)).replaceAll("<replace:\""+key+"\">", getPreparedString(key)).replaceAll("<replace:"+key+">", getPreparedString(key));
        }
        return result;
    }


    private List<String> getKeys() {
        List<String> keys = new ArrayList<String>();
        for (ConfigurationNode node : root.childrenList()) {
            for (ConfigurationNode node1 : node.childrenList()) {
                for (ConfigurationNode node2 : node1.childrenList()) {
                    keys.add(node.key()+"."+node1.key()+"."+node2.key());
                }
                keys.add(node.key()+"."+node1.key());
            }
            keys.add(node.key().toString());
        }
        return keys;
    }

    public @Nullable List<String> getList(@NotNull String path) throws InvalidNodeException {
        ConfigurationNode node = root.node(path);
        if (node.isNull()) throw new InvalidNodeException("[QC] Node " + path + " in configuration " + this.path + " does not exist!");
        try {
            return node.getList(String.class);
        } catch (SerializationException e) {
            throw new RuntimeException("[QC] Node " + path + " in configuration " + this.path + " appears to not be a list: " + e);
        }
    }


    /**
     * Changes the value on a given path or creates a new value
     * @param path The path of the node
     * @param value The new value
     * @throws SerializationException
     */
    public void set(@NotNull String path, String value) throws SerializationException {
        root.node(path).set(value);
    }
    
    
    public boolean has(@NotNull String path) {
        return !root.node(path).isNull();
    }



    /**
     * @return The plugin, the configuration is registered by
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }

}
