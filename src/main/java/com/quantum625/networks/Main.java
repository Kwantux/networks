package com.quantum625.networks;

import com.quantum625.networks.commands.CommandListener;
import com.quantum625.networks.commands.LanguageModule;
import com.quantum625.networks.commands.TabCompleter;
import com.quantum625.networks.data.Config;
import com.quantum625.networks.listener.BlockBreakEventListener;
import com.quantum625.networks.listener.RightClickEventListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.io.File;


public final class Main extends JavaPlugin {

    private File dataFolder;
    private CommandListener commandListener;
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
        this.commandListener = new CommandListener(net, dataFolder, lang, config);
        this.tabCompleter = new TabCompleter(net);

        loadCommands();
        net.loadData();
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }


    }

    private void loadCommands() {
        getCommand("network").setExecutor(commandListener);
        getCommand("network").setTabCompleter(tabCompleter);
        this.getServer().getPluginManager().registerEvents(new BlockBreakEventListener(net, lang), this);
        this.getServer().getPluginManager().registerEvents(new RightClickEventListener(net, lang, config), this);

    }

    @Override
    public void onDisable() {
        //config.save();
        net.saveData();
        Bukkit.getLogger().info("\n\n==================================\n   Networks Plugin was shut down\n==================================\n");
    }
}
