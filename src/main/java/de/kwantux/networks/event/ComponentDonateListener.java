package de.kwantux.networks.event;

import de.kwantux.networks.Main;
import de.kwantux.networks.Network;
import de.kwantux.networks.Sorter;
import de.kwantux.networks.component.BasicComponent;
import de.kwantux.networks.component.module.Donator;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

import javax.annotation.Nullable;

import static de.kwantux.networks.Main.dcu;

public class ComponentDonateListener implements Listener {

    public ComponentDonateListener(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void check(@Nullable org.bukkit.Location location) {
        if (location == null) return;
        BasicComponent component = dcu.componentAtLoadedBlock(location.getBlock());
        if (component != null) {
            if (component instanceof Donator donator) {
                Network network = component.network();
                if (network != null) Sorter.donate(network, donator);
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
        if (!location.isChunkLoaded()) return;
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
