package net.quantum625.networks.listener;

import net.quantum625.networks.data.CraftingManager;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (NamespacedKey key : CraftingManager.recipes) {
            event.getPlayer().discoverRecipe(key);
        }
    }

}
