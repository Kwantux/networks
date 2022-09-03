package com.quantum625.networks.container;

import com.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Inventory;

public class BaseContainer{

    private Location pos;


    public BaseContainer(Location pos) {
        this.pos = pos;
    }

    public BaseContainer(int x, int y, int z, String dim) {
        this.pos = new Location(x, y, z, dim);
    }


    public Location getPos() {
        return pos;
    }

    public void setPos(Location pos) {
        this.pos = pos;
    }


    public Inventory getInventory() {
        Block block = Bukkit.getWorld(pos.getDim()).getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        if (block.getState() instanceof InventoryHolder) {
            return ((InventoryHolder) block.getState()).getInventory();
        }
        return null;
    }
}
