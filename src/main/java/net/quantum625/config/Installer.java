package net.quantum625.config;

import org.bukkit.plugin.java.JavaPlugin;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Installer {

    private final Logger logger;
    private final File dataFolder;
    private final JavaPlugin plugin;
    public Installer(File dataFolder, JavaPlugin plugin) {

        this.logger = plugin.getLogger();

        File networksFolder = new File(dataFolder, "networks");
        if (!networksFolder.exists()) {
            networksFolder.mkdirs();
        }
        this.dataFolder = dataFolder;
        this.plugin = plugin;

        File configfile = new File(dataFolder, "config.yml");
        if (!configfile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        File recipefile = new File(dataFolder, "recipes.yml");
        if (!recipefile.exists()) {
            plugin.saveResource("recipes.yml", false);
        }

        installLanguage("en");
        installLanguage("de");
    }

    public void installLanguage(String id) {
        File langfile = new File(dataFolder, "lang/"+id+".yml");
        if (!langfile.exists()) {
            plugin.saveResource("lang/"+id+".yml", false);
        }
    }

    public void updateConfig(String path, FileConfiguration oldConfig) {
        plugin.saveResource(path, true);
        File configFile = new File(dataFolder, path);
        FileConfiguration newConfig = YamlConfiguration.loadConfiguration(configFile);

        for (String key : oldConfig.getKeys(true)) {
            newConfig.set(key, oldConfig.get(key));
        }

        try {
            newConfig.save(configFile);
        }
        catch (IOException e) {
            logger.warning(e.getMessage());
        }
    }

    public void overwriteLanguage(String id) {
        plugin.saveResource("lang/"+id+".yml", true);
    }

    public void overwriteConfig() {
        plugin.saveResource("config", true);
    }

    public void overwriteAll() {
        overwriteConfig();
        overwriteLanguage("en");
        overwriteLanguage("de");
    }

}
