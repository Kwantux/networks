package com.quantum625.networks;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Installer {

    private File dataFolder;
    private JavaPlugin plugin;
    public Installer(File dataFolder, JavaPlugin plugin) {
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

        installLanguage("en");
        installLanguage("de");
    }

    public void installLanguage(String id) {
        File langfile = new File(dataFolder, "lang/"+id+".yml");
        if (!langfile.exists()) {
            plugin.saveResource("lang/"+id+".yml", false);
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
