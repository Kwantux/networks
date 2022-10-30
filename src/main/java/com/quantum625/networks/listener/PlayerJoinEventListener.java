package com.quantum625.networks.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().discoverRecipe(new NamespacedKey("networks", "wand"));
        event.getPlayer().discoverRecipe(new NamespacedKey("networks", "input_container"));
        event.getPlayer().discoverRecipe(new NamespacedKey("networks", "sorting_container"));
        event.getPlayer().discoverRecipe(new NamespacedKey("networks", "misc_container"));
    }
}
