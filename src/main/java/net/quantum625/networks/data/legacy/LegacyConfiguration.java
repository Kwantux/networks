package net.quantum625.networks.data.legacy;

import net.quantum625.config.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;


public class LegacyConfiguration {

    protected JavaPlugin plugin;
    protected File file;
    protected String path;
    protected FileConfiguration config;
    protected Logger logger;

    public LegacyConfiguration(JavaPlugin plugin, String path) {
        this.plugin = plugin;
        this.path = path;
        this.file = new File(plugin.getDataFolder(), path);
        this.logger = plugin.getLogger();

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void updateTo(Configuration newConfig) {
        for (Map.Entry<String, Object> entry : config.getValues(true).entrySet()) {
            newConfig.set(entry.getKey(), entry.getValue());
        }
    }
}