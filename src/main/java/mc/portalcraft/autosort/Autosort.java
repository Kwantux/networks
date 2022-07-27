package mc.portalcraft.autosort;

import mc.portalcraft.autosort.commands.CommandListener;
import mc.portalcraft.autosort.data.Config;
import mc.portalcraft.autosort.data.Network;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;


public final class Autosort extends JavaPlugin {

    private CommandListener listener = new CommandListener();
    private Config config;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("\n\n==================================\n   Autosort Plugin has launched\n==================================\n");
        loadCommands();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        this.config = new Config(this);

    }

    private void loadCommands() {
        getCommand("autosort").setExecutor(listener);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("\n\n==================================\n   Autosort Plugin was shut down\n==================================\n");
        config.save();
        if(listener.saveData()) {
            Bukkit.getLogger().info("[Autosort] Sucessfully saved networks");
        }
        Bukkit.getLogger().info("[Autosort] Successfully saved config.yml");
    }
}
