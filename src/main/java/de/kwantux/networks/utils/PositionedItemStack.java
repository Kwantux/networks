package de.kwantux.networks.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class PositionedItemStack extends ItemStack {

    private final Inventory inventory;
    private final int slot;

    public PositionedItemStack(ItemStack stack, Inventory inventory, int slot) {
        super(stack);
        this.inventory = inventory;
        this.slot = slot;
    }

    public Inventory inventory() {
        return inventory;
    }

    public int slot() {
        return slot;
    }


    public static Set<PositionedItemStack> fromInventory(@Nullable org.bukkit.inventory.Inventory inventory) {
        Set<PositionedItemStack> stacks = new HashSet<>();
        if (inventory == null) return stacks;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack != null) {
                stacks.add(new PositionedItemStack(stack, inventory, i));
            }
        }
        return stacks;
    }
}
