package dev.nanoflux.networks.event;

import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Manager;
import dev.nanoflux.networks.Network;
import dev.nanoflux.networks.Sorter;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.component.module.Donator;
import dev.nanoflux.networks.component.module.Requestor;
import dev.nanoflux.networks.utils.BlockLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

public class ComponentListener implements Listener {

    private Manager manager;

    public ComponentListener(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.manager = plugin.getNetworkManager();
    }

    private void check(org.bukkit.Location location) {
        BlockLocation loc = new BlockLocation(location);
        NetworkComponent component = manager.getComponent(loc);
        if (component != null) {
            if (component instanceof Donator donator) {
                Network network = manager.getNetworkWithComponent(component.pos());
                if (network != null) Sorter.donate(network, donator);
            }
            if (component instanceof Requestor requestor) {
                Network network = manager.getNetworkWithComponent(component.pos());
                if (network != null) Sorter.request(network, requestor);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        check(event.getInventory().getLocation());
    }

    @EventHandler
    public void onItemTransmit(InventoryMoveItemEvent event) {
        check(event.getDestination().getLocation());
    }

    @EventHandler
    public void onItemPickup(InventoryPickupItemEvent event) {
        check(event.getInventory().getLocation());
    }

}
