package mc.portalcraft.autosort.data;

import mc.portalcraft.autosort.Autosort;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class BaseFile {
    private Autosort main;
    private File file;
    public FileConfiguration config;

    public BaseFile(Autosort main, String filename) {
        this.main = main;
        this.file = new File(main.getDataFolder(), filename);

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

    public void save() {
        if (this.config == null) {
            this.config = YamlConfiguration.loadConfiguration(new File("config"));
            Bukkit.getLogger().warning("No ");
        }
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
