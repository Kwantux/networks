package de.kwantux.config.lang;

import de.kwantux.config.util.exceptions.InvalidNodeException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class LanguageController {

    private final JavaPlugin plugin;
    private final Logger logger;
    private final MiniMessage mm;

    private final List<Language> languages = new ArrayList<Language>();

    public LanguageController(JavaPlugin plugin, String... order) {
        this.plugin = plugin;
        logger = plugin.getLogger();
        mm = MiniMessage.miniMessage();

        List<String> orderString = new ArrayList<String>();

        for (String key : order) {
            try {
                if (!orderString.contains(key)) {
                    languages.add(new Language(plugin, key));
                    orderString.add(key);
                }
            } catch (SerializationException e) {
                logger.severe("[QC] Language file " + key + ".yml does not exist!");
            }
        }
        File[] files = new File(plugin.getDataFolder(), "lang/").listFiles();
        for (File file : files) {
            try {
                String key = file.getName().toLowerCase().replaceAll(".yml", "");
                if (!orderString.contains(key)) {
                    languages.add(new Language(plugin, key));
                    orderString.add(key);
                }
            } catch (SerializationException ignored) {}
        }

        Collections.reverse(languages);

        logger.info("[QC] Launched using language order: " + orderString);
    }

    /**
     * Gives you the raw string of the given node
     * @param path The path of the requested node
     * @return Raw value of the given node
     * @throws InvalidNodeException When you give a non-existent node
     */
    public String getRaw(@NotNull String path) throws InvalidNodeException {
        String result = null;
        for (Language language : languages) {
            if (language.has(path)) {
                result = language.getRaw(path);
            }
        }
        if (result != null) return result;
        throw new InvalidNodeException("[QC] No language file with language key " + path + " found!");
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
     * @return the given node
     * @throws InvalidNodeException When you give a non-existent node
     */
    public Component getFinal(String path) {
        try {
            return mm.deserialize(getPreparedString(path));
        } catch (InvalidNodeException e) {
            logger.severe("Invalid language key: " + path);
            return Component.text(plugin.getName() + "." + path);
        }
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



    private @NotNull String getPreparedString(@NotNull String path) throws InvalidNodeException {
        String result = null;
        for (Language language : languages) {
            if (language.has(path)) {
                result = language.getPreparedString(path);
            }
        }
        if (result != null) return result;
        throw new InvalidNodeException("[QC] No language file with language key " + path + " found!");
    }



    public @Nullable List<String> getList(@NotNull String path) throws InvalidNodeException {
        List<String> result = null;
        for (Language language : languages) {
            if (language.has(path)) {
                result = language.getList(path);
            }
        }
        if (result != null) return result;
        throw new InvalidNodeException("[QC] No language file with language key " + path + " found!");
    }

    public @Nullable Component getItemName(@NotNull String path) throws InvalidNodeException{
        return get("item.name."+path).decoration(TextDecoration.ITALIC, false);
    }
    public List<Component> getItemLore(String path) throws InvalidNodeException {
        List<String> list = getList("item.lore."+path);
        List<Component> result = new ArrayList<Component>();
        for (String s : list) {
            result.add(mm.deserialize(s).decoration(TextDecoration.ITALIC, false));
        }
        return result;
    }

    private Component invalidKeyError(String key) {
        return Component.text("[QC]").color(TextColor.color(0, 255, 100)).append(Component.text(" Invalid language key ").color(TextColor.color(255, 30, 30))).append(Component.text(key).color(TextColor.color(255, 255, 255))).append(Component.text(" not found in any language file!").color(TextColor.color(255, 30, 30)));
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
}
