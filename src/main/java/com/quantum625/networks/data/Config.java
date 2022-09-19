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

    private String[] container_whitelist = {"CHEST", "REDSTONE_CHEST", "BARREL"};

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
    }

    public void setLanguage(String language) {
        config.set("lang", language);
    }

    public String getLanguage() {
        return config.get("lang").toString();
    }

    public boolean checkLocation(Location location, String component) {
        String[] whitelist = container_whitelist;
        Bukkit.getLogger().info(Arrays.asList(config.get(component + "_whitelist")).toString());
        Bukkit.getLogger().info(location.getBukkitLocation().getBlock().getType().toString().toUpperCase());

        return (Arrays.asList(container_whitelist).contains(location.getBukkitLocation().getBlock().getType().toString().toUpperCase()));
    }
}
