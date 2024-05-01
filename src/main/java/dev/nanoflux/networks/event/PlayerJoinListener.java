package dev.nanoflux.networks.event;

import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.CraftingManager;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    public PlayerJoinListener(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (NamespacedKey key : CraftingManager.recipes) {
            System.out.println(key);
            event.getPlayer().discoverRecipe(key);
        }
    }

}
