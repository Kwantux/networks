package net.quantum625.networks.listener;

import net.quantum625.networks.Main;
import net.quantum625.networks.Network;
import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.component.InputContainer;
import net.quantum625.networks.utils.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class HopperCollectEventListener implements Listener {

    public final NetworkManager net;

    public HopperCollectEventListener(Main main) {
        this.net = main.getNetworkManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemPickup(InventoryPickupItemEvent event) {
        if (event.isCancelled()) return;
        if (event.getInventory().getType().equals(InventoryType.HOPPER)) {
            // Hoppers should always have a location
            assert event.getInventory().getLocation() != null;
            Location location = new Location(event.getInventory().getLocation());
            Network network = net.getNetworkWithComponent(location);
            if (network == null) return;
            InputContainer container = network.getInputContainerByLocation(location);
            if (container != null) {
                ItemStack stack = event.getItem().getItemStack();
                net.sortItem(stack, location, container.getInventory());
                event.setCancelled(true);
                event.getItem().remove();
            }
        }
    }
}
