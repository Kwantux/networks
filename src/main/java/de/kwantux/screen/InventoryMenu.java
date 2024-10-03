package de.kwantux.screen;

import org.bukkit.inventory.ItemStack;

public class InventoryMenu {

    private int size = 3;
    private ItemStack[] contents;

    public InventoryMenu(int size) {
        this.size = size;
        this.contents = new ItemStack[size];
    }

    public int size() {
        return size;
    }

    public ItemStack[] contents() {
        return contents;
    }
}
