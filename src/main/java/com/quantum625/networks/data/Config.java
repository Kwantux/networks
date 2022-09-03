package com.quantum625.networks.data;

import com.quantum625.networks.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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
        setLanguage("de");
    }

    public void setLanguage(String language) {
        config.set("lang", language);
    }

    public String getLanguage() {
        return config.get("lang").toString();
    }
}
