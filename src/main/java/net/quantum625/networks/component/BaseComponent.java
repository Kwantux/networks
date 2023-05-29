package net.quantum625.networks.component;

import net.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BaseComponent {

    protected Location pos;

    public BaseComponent(Location pos) {
        this.pos = pos;
    }


    public Location getPos() {
        return pos;
    }


    public ComponentType getType() {return ComponentType.EMPTY;}


    public Inventory getInventory() {
        Block block = Bukkit.getWorld(pos.getDim()).getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        if (block.getState() instanceof InventoryHolder) {
            return ((InventoryHolder) block.getState()).getInventory();
        }
        return null;
    }

    public int countItems() {

        int result = 0;

        for (ItemStack stack : getInventory()) {
            if (stack != null) result += stack.getAmount();
        }

        return result;
    }
}
