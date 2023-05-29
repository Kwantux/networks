package net.quantum625.networks.listener;

import net.quantum625.networks.data.Config;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventListener implements Listener {


    Config config;

    private String[] modes = {"input", "sorting", "misc"};
    private String[] types = {"barrel", "chest", "dispenser", "dropper", "hopper", "trapped_chest"};


    public PlayerJoinEventListener(Config config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        discover(event.getPlayer());
    }

    public void discover(Player player) {
        player.discoverRecipe(new NamespacedKey("networks", "wand"));

        for (String mode: modes) {
            for (String type : types) {
                player.discoverRecipe(new NamespacedKey("networks", mode + "_container_" + type));
            }
        }

        for (int i = 1; i <= config.getMaxRanges().length; i++) {
            player.discoverRecipe(new NamespacedKey("networks","upgrade_" + i));
        }
    }
}
