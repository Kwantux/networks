package dev.nanoflux.networks.component;

import dev.nanoflux.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Inventory;

public abstract class NetworkComponent {

    protected static ComponentType type;

    public static ComponentType type() {
        return type;
    }

    protected Location pos;

    protected NetworkComponent(Location pos) {
        this.pos = pos;
    }

    public Location pos() {
        return pos;
    }

    public Inventory inventory() {
        Block block = Bukkit.getWorld(pos.getWorld()).getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        if (block.getState() instanceof InventoryHolder) {
            return ((InventoryHolder) block.getState()).getInventory();
        }
        // TODO: Remove Component from database
        return null;
    }
}
