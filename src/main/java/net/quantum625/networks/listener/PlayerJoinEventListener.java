package net.quantum625.networks.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventListener implements Listener {

    private String[] modes = {"input", "sorting", "misc"};
    private String[] types = {"barrel", "chest", "dispenser", "dropper", "hopper", "trapped_chest"};

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().discoverRecipe(new NamespacedKey("networks", "wand"));

        for (String mode: modes) {
            for (String type : types) {
                event.getPlayer().discoverRecipe(new NamespacedKey("networks", mode + "_container_" + type));
            }
        }
    }
}
