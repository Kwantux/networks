package de.kwantux.networks.event;

import de.kwantux.networks.Main;
import de.kwantux.networks.Manager;
import de.kwantux.networks.Network;
import de.kwantux.networks.Sorter;
import de.kwantux.networks.component.NetworkComponent;
import de.kwantux.networks.component.module.Donator;
import de.kwantux.networks.component.module.Requestor;
import de.kwantux.networks.utils.BlockLocation;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

import javax.annotation.Nullable;

public class ComponentListener implements Listener {

    private Manager manager;

    public ComponentListener(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.manager = plugin.getNetworkManager();
    }

    private void check(@Nullable org.bukkit.Location location) {
        if (location == null) return;
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
        Location location = event.getDestination().getLocation();
        if (location == null) return;
        Main.regionScheduler.execute(
                Main.instance,
                location,
                () -> check(location)
        );

    }

    @EventHandler
    public void onItemPickup(InventoryPickupItemEvent event) {
        Location location = event.getInventory().getLocation();
        if (location == null) return;
        Main.regionScheduler.execute(
                Main.instance,
                location,
                () -> check(location)
        );
    }

}
