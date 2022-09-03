package com.quantum625.autosort;

import com.quantum625.autosort.commands.CommandListener;
import com.quantum625.autosort.commands.TabCompleter;
import com.quantum625.autosort.data.Config;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.io.File;


public final class Autosort extends JavaPlugin {

    private File dataFolder;
    private CommandListener listener;
    private TabCompleter tabCompleter;
    private NetworkManager net;
    private Config config;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("\n\n==================================\n   Autosort Plugin has launched\n==================================\n");
        this.dataFolder = this.getDataFolder();
        this.config = new Config(this);
        this.net = new NetworkManager(this.dataFolder);
        this.listener = new CommandListener(net, dataFolder, config.getLanguage());
        this.tabCompleter = new TabCompleter(net);
        loadCommands();
        listener.loadData();
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }


    }

    private void loadCommands() {
        getCommand("autosort").setExecutor(listener);
        getCommand("autosort").setTabCompleter(tabCompleter);
    }

    @Override
    public void onDisable() {
        config.save();
        listener.saveData();
        Bukkit.getLogger().info("\n\n==================================\n   Autosort Plugin was shut down\n==================================\n");
    }
}
