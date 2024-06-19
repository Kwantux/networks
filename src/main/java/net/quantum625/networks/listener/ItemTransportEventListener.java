package net.quantum625.networks.listener;

import it.unimi.dsi.fastutil.objects.Object2ReferenceArrayMap;
import net.quantum625.networks.Main;
import net.quantum625.networks.Network;
import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.component.InputContainer;
import net.quantum625.networks.utils.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemTransportEventListener implements Listener {
    private final NetworkManager net;

    public ItemTransportEventListener(Main main) {
        net = main.getNetworkManager();
    }

    @EventHandler(priority= EventPriority.HIGH)
    public void onItemTransport(InventoryMoveItemEvent event) {
        org.bukkit.Location loc = event.getDestination().getLocation();
        if (loc == null) return;
        Location location = new Location(loc);
        Network network = net.getNetworkWithComponent(location);
        if (network == null) return;
        InputContainer container = network.getInputContainerByLocation(location);
        if (container != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    network.sort(container.getPos());
                }
            }.runTask(Main.getInstance());
        }
    }
}
