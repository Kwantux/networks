package mc.portalcraft.autosort.data;

import mc.portalcraft.autosort.Autosort;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    private Autosort main;
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
    public Config(Autosort main) {
        this.main = main;
        this.file = new File(main.getDataFolder(), "config.yml");

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
}
