package net.quantum625.networks.listener;

import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.utils.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseEventListener implements Listener {

    public final NetworkManager net;

    public InventoryCloseEventListener(NetworkManager net) {
        this.net = net;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getLocation() != null) {
            net.sortContainer(new Location(event.getInventory().getLocation()));
        }
    }
}
