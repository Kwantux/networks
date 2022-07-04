package mc.portalcraft.autosort;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.util.UUID;


public final class Autosort extends JavaPlugin {

    private NetworkManager net = new NetworkManager();
    @Override
    public void onEnable() {
        Bukkit.getLogger().info("\n\n==================================\n   Autosort Plugin has launched\n==================================\n");
        getCommand("autosort").setExecutor(new CommandListener());

        StorageNetwork sn = new StorageNetwork("test", UUID.fromString("58cd7b4f-4fdd-4aa6-b6a4-6e8b6adc998e"));
        sn.addInputChest(new Location(getServer().getWorld("world"), 3,3,3));
        sn.sort(new Location(getServer().getWorld("world"), 3,3,3));

        if (net.loadData()) {
            Bukkit.getLogger().info("Sucessfully loaded network file!");
        }
        else {
            Bukkit.getLogger().warning("Failed loading network file!");
        }

    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("\n\n==================================\n   Autosort Plugin was shut down\n==================================\n");
        if (net.saveData()) {
            Bukkit.getLogger().info("Sucessfully saved network file!");
        }
        else {
            Bukkit.getLogger().warning("Failed saving network file!");
        }
    }
}
