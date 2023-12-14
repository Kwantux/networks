package quantum625.networks.listener;

import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Network;
import dev.nanoflux.networks.Manager;
import dev.nanoflux.networks.component.component.InputContainer;
import dev.nanoflux.networks.utils.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemTransportEventListener implements Listener {
    private final Manager net;

    public ItemTransportEventListener(Main main) {
        net = main.getNetworkManager();
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onItemTransport(InventoryMoveItemEvent event) {
        org.bukkit.Location loc = event.getDestination().getLocation();
        if (loc == null) return;
        Location location = new Location(loc);
        Network network = net.getNetworkWithComponent(location);
        if (network == null) return;
        InputContainer container = network.getInputContainerByLocation(location);
        if (container != null) {
            net.sortItem(event.getItem().clone(), location, container.getInventory());
            event.setItem(new ItemStack(Material.AIR));
        }
    }
}
