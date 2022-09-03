package com.quantum625.autosort.data;

import com.quantum625.autosort.Autosort;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Dictionary;

public class Language {
    String lang_id;

    private File file;
    public FileConfiguration config;



    public Language(File datafolder, String lang_id) {
        this.lang_id = lang_id;
        this.file = new File(datafolder, "lang/" + lang_id + ".yml");


        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        Bukkit.getLogger().info("[Autosort] Launched using language module " + lang_id);
    }

    public String getConsoleText(String id) {
        return config.get(id).toString();
    }

    public String getPlayerText(String id) {
        return config.get(id).toString();
    }

}
