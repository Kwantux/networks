package de.kwantux.networks.event;

import de.kwantux.networks.Main;
import de.kwantux.networks.config.CraftingManager;
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
            event.getPlayer().discoverRecipe(key);
        }
    }

}
