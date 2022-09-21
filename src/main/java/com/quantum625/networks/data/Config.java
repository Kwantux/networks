package com.quantum625.networks.data;

import com.quantum625.networks.Main;
import com.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Config {

    private Main plugin;
    private File file;
    public FileConfiguration config;

    public void save() {
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Config(Main plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);

        if (config.get("lang") == null) {
            config.set("lang", "en");
            Bukkit.getLogger().warning("Config for language is missing, it was reset to en");
        }

        if (config.get("tickrate") == null) {
            config.set("tickrate", 100);
            Bukkit.getLogger().warning("Config for tickrate is missing, it was reset to 100");
        }

        if (config.get("container_whitelist") == null) {
            config.set("container_whitelist", Arrays.asList("CHEST", "REDSTONE_CHEST", "BARREL"));
            Bukkit.getLogger().warning("Config for container_whitelist is missing, it was reset to [CHEST, REDSTONE_CHEST, BARREL]");
        }
    }

    public void setLanguage(String language) {
        config.set("lang", language);
    }

    public String getLanguage() {
        return config.get("lang").toString();
    }

    public void setTickrate(int tickrate) {config.set("tickrate", tickrate);}
    public int getTickrate() {return Integer.parseInt(config.get("tickrate").toString());}

    public boolean checkLocation(Location location, String component) {
        return (Arrays.asList(config.get("container_whitelist")).contains(location.getBukkitLocation().getBlock().getType().toString().toUpperCase()));
    }
}
