package net.quantum625.networks.listener;

import net.quantum625.config.lang.Language;
import net.quantum625.networks.Main;
import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.data.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;


public class InventoryOpenEventListener implements Listener {
    private final NetworkManager net;

    public InventoryOpenEventListener(Main main) {
        net = main.getNetworkManager();
    }

    @EventHandler(priority= EventPriority.LOW)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().firstEmpty() == -1) {
            net.noticePlayer((Player) event.getPlayer());
        }
    }
}
