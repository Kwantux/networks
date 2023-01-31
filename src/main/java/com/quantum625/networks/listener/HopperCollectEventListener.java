package com.quantum625.networks.listener;

import com.quantum625.networks.NetworkManager;
import com.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;

public class HopperCollectEventListener implements Listener {

    public final NetworkManager net;

    public HopperCollectEventListener(NetworkManager net) {
        this.net = net;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryPickupItemEvent event) {
        if (event.getInventory().getType().equals(InventoryType.HOPPER)) {
            net.sortContainer(new Location(event.getInventory().getLocation()));
        }
    }
}
