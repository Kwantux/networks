package net.quantum625.config.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.quantum625.config.RawConfiguration;
import net.quantum625.config.util.exceptions.InvalidNodeException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.N;
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Language extends RawConfiguration {

    private final MiniMessage mm;

    private final String langID;
    private final YamlConfigurationLoader loader;
    
    private ConfigurationNode root;

    public Language(@NotNull JavaPlugin plugin, @NotNull String langID) throws SerializationException {

        this.mm = MiniMessage.miniMessage();

        this.plugin = plugin;
        this.langID = langID;

        this.ingameEdit = true;

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

            try {
                ingameEdit = Boolean.parseBoolean(getRaw("allowIngameEdit"));
            }
            catch (InvalidNodeException e) {
                root.node("allowIngameEdit").set(ingameEdit);
            }

        } catch (final ConfigurateException e) {
            logger.severe("[QC] An error occurred while loading configuration " + langID + ".yml: " + e.getMessage());
            new RuntimeException(e);
        }

        logger.info("[QC] Successfully loaded configuration file " + langID + ".yml on root path " + path);
    }



    /**
     * Saves the configuration file
     * @Warning: This will override the existing file!
     */
    public void save() {
        try {
            root.node("allowIngameEdit").set(ingameEdit);
        }
        catch (SerializationException e) {
            logger.severe("[QC] Unable to edit configuration file entry 'allowIngameEdit' in file " + langID + ".yml:\n" + e.getMessage());
        }
        try {
            loader.save(root);
        } catch (final ConfigurateException e) {
            logger.severe("[QC] Unable to save configuration file " + langID + ".yml:\n" + e.getMessage());
        }
    }


    /**
     * Enables the /config edit command for this file
     * (Disabled by default)
     */
    public void enableIngameChange() {
        ingameEdit = true;
        try {
            root.node("allowIngameEdit").set(ingameEdit);
        }
        catch (SerializationException e) {
            logger.severe("[QC] Unable to edit configuration file entry 'allowIngameEdit' in file " + langID + ".yml:\n" + e.getMessage());
        }
    }


    /**
     * Disables the /config edit command for this file
     * (Disabled by default)
     */
    public void disableIngameChange() {
        ingameEdit = false;
        try {
            root.node("allowIngameEdit").set(ingameEdit);
        }
        catch (SerializationException e) {
            logger.severe("[QC] Unable to edit configuration file entry 'allowIngameEdit' in file " + langID + ".yml:\n" + e.getMessage());
        }
    }


    /**
     * @return Whether, using /config edit is allowed for this config file
     */
    public boolean ingameChangeEnabled() {
        return ingameEdit;
    }

    



    /**
     * @param path The path of the requested node
     * @return the given node
     * @throws InvalidNodeException When you give a non-existent node
     */
    public Component get(String path) throws InvalidNodeException {
        return mm.deserialize(getPreparedString(path));
    }


    /**
     * @param path The path of the requested node
     * @param replacements Will replace <1>, <2>... in the text with the given components (Start with <1>!)
     * @return the given node
     * @throws InvalidNodeException When you give a non-existent node
     */
    public Component get(String path, Component... replacements) throws InvalidNodeException {
        List<TagResolver> resolvers = new ArrayList<TagResolver>();
        for (int i = 0; i < replacements.length; i++) {
            resolvers.add(Placeholder.component(String.valueOf(i+1), replacements[i]));
        }
        return mm.deserialize(getPreparedString(path), resolvers.toArray(new TagResolver[replacements.length]));
    }


    /**
     * @param path The path of the requested node
     * @param replacements TagResolvers, that should be applied to the deserializer
     * @return the given node
     * @throws InvalidNodeException When you give a non-existent node
     */
    public Component get(String path, TagResolver... replacements) throws InvalidNodeException {
        return mm.deserialize(getPreparedString(path), replacements);
    }

    /**
     * @param path The path of the requested node
     * @param replacements Will replace <1>, <2>... in the text with the given components (Start with <1>!)
     * @return the given node
     * @throws InvalidNodeException When you give a non-existent node
     */
    public Component get(String path, String... replacements) throws InvalidNodeException {
        List<TagResolver> resolvers = new ArrayList<TagResolver>();
        for (int i = 0; i < replacements.length; i++) {
            resolvers.add(Placeholder.component(String.valueOf(i+1), Component.text(replacements[i])));
        }
        return mm.deserialize(getPreparedString(path), resolvers.toArray(new TagResolver[replacements.length]));
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

    private @NotNull String getPreparedString(@NotNull String path) throws InvalidNodeException {
        String result = getRaw(path);
        ConfigurationNode prefix = root.node("prefix");
        if (!prefix.isNull()) result = prefix.getString()+"<reset>"+result;
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

    public @Nullable String getItemName(@NotNull String path) throws InvalidNodeException{
        return getRaw("item.name."+path);
    }
    public List<String> getItemLore(String path) throws InvalidNodeException, SerializationException {
        return getList("item.lore."+path);
    }

    private Component invalidKeyError(String key) {
        return Component.text("[QC]").color(TextColor.color(0, 255, 100)).append(Component.text(" Invalid language key ").color(TextColor.color(255, 30, 30))).append(Component.text(key).color(TextColor.color(255, 255, 255))).append(Component.text(" not found in language file!").color(TextColor.color(255, 30, 30)));
    }


    /**
     * Translates a key to a message and sends this message to a given reciever
     * @param receiver Either a player or the console
     * @param key The registry key of the text, you want to send
     * @return true - When the message was successfully sent  |  false - When the language key doesn't exist
     */
    public boolean message(CommandSender receiver, String key) {
        try {
            receiver.sendMessage(get(key));
            return true;
        }
        catch (InvalidNodeException e) {
            receiver.sendMessage(invalidKeyError(key));
            return false;
        }
    }



    /**
     * Translates a key to a message and sends this message to a given reciever
     * @param receiver Either a player or the console
     * @param key The registry key of the text, you want to send
     * @return true - When the message was successfully sent  |  false - When the language key doesn't exist
     */
    public boolean message(CommandSender receiver, String key, Component... replacements) {
        try {
            receiver.sendMessage(get(key, replacements));
            return true;
        }
        catch (InvalidNodeException e) {
            receiver.sendMessage(invalidKeyError(key));
            return false;
        }
    }

    /**
     * Translates a key to a message and sends this message to a given reciever
     * @param receiver Either a player or the console
     * @param key The registry key of the text, you want to send
     * @return true - When the message was successfully sent  |  false - When the language key doesn't exist
     */
    public boolean message(CommandSender receiver, String key, String... replacements) {
        try {
            receiver.sendMessage(get(key, replacements));
            return true;
        }
        catch (InvalidNodeException e) {
            receiver.sendMessage(invalidKeyError(key));
            return false;
        }
    }





    /**
     * Translates a key to a message and sends this message to a given reciever
     * @param receiver Either a player or the console
     * @param key The registry key of the text, you want to send
     * @return true - When the message was successfully sent  |  false - When the language key doesn't exist
     */
    public boolean message(CommandSender receiver, String key, TagResolver... replacements) {
        try {
            receiver.sendMessage(get(key, replacements));
            return true;
        }
        catch (InvalidNodeException e) {
            receiver.sendMessage(invalidKeyError(key));
            return false;
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
