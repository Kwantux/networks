package net.quantum625.networks.listener;

import net.quantum625.networks.Main;
import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.component.BaseComponent;
import net.quantum625.networks.utils.DoubleChestUtils;
import net.quantum625.networks.utils.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseEventListener implements Listener {

    private final NetworkManager net;
    private final DoubleChestUtils dcu;

    public InventoryCloseEventListener(Main main, DoubleChestUtils dcu) {
        this.net = main.getNetworkManager();
        this.dcu = dcu;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getLocation() != null) {
            BaseComponent component = dcu.componentAt(new Location(event.getInventory().getLocation()));
            if (component == null) return;
            net.sortContainer(component.getPos());
        }
    }
}
