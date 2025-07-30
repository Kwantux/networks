package de.kwantux.manual;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Manual {

    private final MiniMessage mm;

    protected JavaPlugin plugin;
    protected Logger logger;

    protected List<String> requirements = new ArrayList<String>();

    protected String path;
    private String language;

    private YamlConfigurationLoader loader;
    private CommentedConfigurationNode root;
    
    private final String id;
    private Book book;

    public Manual(JavaPlugin plugin, String id, String language) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        this.path = plugin.getName().toLowerCase() + ".manual." + language ;
        this.id = id;

        this.mm = MiniMessage.miniMessage();

        this.logger = plugin.getLogger();
        
        this.language = language;

        build();
        ManualManager.register(this);
    }


    /**
     * Adds a path to the list of required options
     * @param path The path of the option, that is required
     */
    public void require(String path) {
        if (!requirements.contains(path)) requirements.add(path);
    }

    /**
     * Adds multiple paths to the list of required options
     * @param paths The paths of the options, that are required
     */

    public void require(List<String> paths) {
        requirements.addAll(paths);
    }

    /**
     * Removes a path from the list of required options
     * @param path The path of the option, that was required
     *
     */
    public void unrequire(String path) {
        requirements.remove(path);
    }


    /**
     * Tests if all the required options are set
     * @return True if all the required options are not null
     */
    public boolean testRequirements() {
        boolean result = true;
        for (String req : requirements) {
            if (!has(req))  {
                logger.severe("[Manuals] File '"+this.path+"' is missing option: " + req);
                result = false;
            }
        }
        return result;
    }

    private boolean has(String req) {
        return !root.node(path).isNull();
    }


    /**
     * Reloads the configuration file
     * @Warning: This will override all settings changed
     */
    public void reload() {
        Path path = Paths.get(plugin.getDataFolder().getAbsolutePath() + "/manuals/" + id + "/" + language + ".yml");
        if (!Files.exists(path)) {
            this.language = "en";
            this.path = plugin.getName().toLowerCase() + ".manual." + language ;
            path = Paths.get(plugin.getDataFolder().getAbsolutePath() + "/manuals/" + id + "/" + language + ".yml");
        }
        this.loader = YamlConfigurationLoader.builder()
                .path(path)
                .build();
        try {
            root = loader.load();

            if (root == null) {
                logger.severe("[Manuals] Failed to load configuration " + language + ".yml, root configuration is null");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }

        } catch (final ConfigurateException e) {
            logger.severe("[Manuals] Could not load configuration " + language + ".yml: Invalid Syntax");
            throw new RuntimeException(e);
        }

        if (testRequirements()) logger.info("[Manuals] Successfully loaded configuration file " + language + ".yml on root path " + path);
    }


    public void build() {

        reload();

        List<Component> pages = new ArrayList<>();

        try {

            for (String node : root.node("pages").getList(String.class)) {
                pages.add(mm.deserialize(node));
            }

            book = Book.book(Component.text(root.node("name").getString()), Component.text(root.node("author").getString()), pages);
        } catch (SerializationException | NullPointerException e) {
            logger.severe("[Manuals] Language file " + this.path + " has an invalid format. Try to restart the server.");
            throw new RuntimeException(e);
        }

        //BookMeta item = BookMeta.builder().author(NetworkComponent.text(author)).title(NetworkComponent.text(name)).pages(pages).build();
    }

    public void show(@NotNull Player player) {
        player.openBook(book);
    }

    public JavaPlugin getPlugin() {return plugin;}

    public String getRegistrationID() {return id + "." + plugin.getName();}
    public Book getBook() {return book;}



}
