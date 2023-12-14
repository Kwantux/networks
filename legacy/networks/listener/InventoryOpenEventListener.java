package quantum625.networks.listener;

import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Manager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryOpenEventListener implements Listener {
    private final Manager net;

    public InventoryOpenEventListener(Main main) {
        net = main.getNetworkManager();
    }

    @EventHandler(priority= EventPriority.LOW)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().firstEmpty() == -1 && event.getInventory().getType().equals(InventoryType.CHEST)) {
            net.noticePlayer((Player) event.getPlayer());
        }
    }
}
