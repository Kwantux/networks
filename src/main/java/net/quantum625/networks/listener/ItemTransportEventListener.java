package net.quantum625.networks.listener;

import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.data.Config;
import net.quantum625.networks.utils.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class ItemTransportEventListener implements Listener {
    private final NetworkManager net;
    private final Config config;

    public ItemTransportEventListener(NetworkManager networkManager, Config config) {
        net = networkManager;
        this.config = config;
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onItemTransport(InventoryMoveItemEvent event) {
        net.sortContainer(new Location(event.getDestination().getLocation()));
    }
}
