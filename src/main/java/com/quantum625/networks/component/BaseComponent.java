package com.quantum625.networks.component;

import com.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Inventory;

public class BaseComponent {

    private Location pos;


    public BaseComponent(Location pos) {
        this.pos = pos;
    }

    public BaseComponent(int x, int y, int z, String dim) {
        this.pos = new Location(x, y, z, dim);
    }


    public Location getPos() {
        return pos;
    }

    public void setPos(Location pos) {
        this.pos = pos;
    }


    public String getType() {return "none";};


    public Inventory getInventory() {
        Block block = Bukkit.getWorld(pos.getDim()).getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        if (block.getState() instanceof InventoryHolder) {
            return ((InventoryHolder) block.getState()).getInventory();
        }
        return null;
    }
}
