package mc.portalcraft.autosort;

import mc.portalcraft.autosort.commands.CommandListener;
import mc.portalcraft.autosort.data.Config;
import mc.portalcraft.autosort.data.Networks;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.util.UUID;


public final class Autosort extends JavaPlugin {

    private NetworkManager net = new NetworkManager();
    private Config config;
    private Networks netlist;
    @Override
    public void onEnable() {
        Bukkit.getLogger().info("\n\n==================================\n   Autosort Plugin has launched\n==================================\n");
        loadCommands();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        this.config = new Config(this);
        this.netlist = new Networks(this);

    }

    private void loadCommands() {
        getCommand("autosort").setExecutor(new CommandListener());
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("\n\n==================================\n   Autosort Plugin was shut down\n==================================\n");
        config.save();
    }
}
