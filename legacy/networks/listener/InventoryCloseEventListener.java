package quantum625.networks.listener;

import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Manager;
import dev.nanoflux.networks.BaseComponent;
import dev.nanoflux.networks.utils.DoubleChestUtils;
import dev.nanoflux.networks.utils.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseEventListener implements Listener {

    private final Manager net;
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
