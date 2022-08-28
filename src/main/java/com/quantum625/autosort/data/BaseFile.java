package com.quantum625.autosort.data;

import com.quantum625.autosort.Autosort;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
