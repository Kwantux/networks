package quantum625.networks.listener;

import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Network;
import dev.nanoflux.networks.Manager;
import dev.nanoflux.networks.component.component.InputContainer;
import dev.nanoflux.networks.utils.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class HopperCollectEventListener implements Listener {

    public final Manager net;

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
