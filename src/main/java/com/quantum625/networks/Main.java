package com.quantum625.networks;

import com.quantum625.networks.commands.CommandListener;
import com.quantum625.networks.commands.LanguageModule;
import com.quantum625.networks.commands.TabCompleter;
import com.quantum625.networks.data.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.io.File;


public final class Main extends JavaPlugin {

    private File dataFolder;
    private CommandListener listener;
    private TabCompleter tabCompleter;
    private NetworkManager net;
    private Config config;

    private LanguageModule lang;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("\n\n==================================\n   Networks Plugin has launched\n==================================\n");
        this.dataFolder = this.getDataFolder();
        this.config = new Config(this);
        this.lang = new LanguageModule(dataFolder, config.getLanguage());
        this.net = new NetworkManager(this.dataFolder);
        this.listener = new CommandListener(net, dataFolder, lang);
        this.tabCompleter = new TabCompleter(net);

        loadCommands();
        net.loadData();
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }


    }

    private void loadCommands() {
        getCommand("network").setExecutor(listener);
        getCommand("network").setTabCompleter(tabCompleter);
        this.getServer().getPluginManager().registerEvents(new BlockBreakEventListener(net, lang), this);

    }

    @Override
    public void onDisable() {
        //config.save();
        net.saveData();
        Bukkit.getLogger().info("\n\n==================================\n   Networks Plugin was shut down\n==================================\n");
    }
}
