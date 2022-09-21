package com.quantum625.networks;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Installer {

    private File dataFolder;
    private JavaPlugin plugin;
    public Installer(File dataFolder, JavaPlugin plugin) {
        this.dataFolder = dataFolder;
        this.plugin = plugin;
        plugin.saveResource("config.yml", false);
        plugin.saveResource("networks", false);
        installLanguage("en");
        installLanguage("de");
    }

    public void installLanguage(String id) {
        plugin.saveResource("lang/"+id+".yml", false);
    }

    public void overwriteLanguage(String id) {
        plugin.saveResource("lang/"+id+".yml", true);
    }

    public void overwriteConfig() {
        plugin.saveResource("config", true);
    }

    public void overwriteAll() {
        plugin.saveResource("networks", true);
        overwriteConfig();
        overwriteLanguage("en");
        overwriteLanguage("de");
    }

}
