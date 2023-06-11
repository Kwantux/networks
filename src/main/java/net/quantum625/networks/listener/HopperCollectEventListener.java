package net.quantum625.networks.listener;

import net.quantum625.networks.Main;
import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.utils.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;

public class HopperCollectEventListener implements Listener {

    public final NetworkManager net;

    public HopperCollectEventListener(Main main) {
        this.net = main.getNetworkManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryPickupItemEvent event) {
        if (event.getInventory().getType().equals(InventoryType.HOPPER)) {
            net.sortContainer(new Location(event.getInventory().getLocation()));
        }
    }
}
