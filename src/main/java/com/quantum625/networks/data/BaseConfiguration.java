package com.quantum625.networks.data;

import com.quantum625.networks.Installer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class BaseConfiguration {

    protected JavaPlugin plugin;
    protected File file;
    protected String path;
    protected FileConfiguration config;
    protected Installer installer;
    protected Logger logger;

    public BaseConfiguration(JavaPlugin plugin, String path, Installer installer) {
        this.plugin = plugin;
        this.path = path;
        this.file = new File(plugin.getDataFolder(), path);
        this.installer = installer;
        this.logger = plugin.getLogger();

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Essential keys the configuration file needs to have
    protected String[] keys = {};

    // Check if all of these keys are present
    public boolean checkForSettings() {
        boolean result = true;
        for (String key : keys) {
            if (config.get(key) == null) {
                logger.warning("Config for key " + key + " is missing, config file was resaved.");
                logger.warning("If you just installed an update for this plugin, ignore this message");
                installer.updateConfig(path, config);
                result = false;
            }
        }
        return result;
    }
}