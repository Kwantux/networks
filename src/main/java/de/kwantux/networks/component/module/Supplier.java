package de.kwantux.networks.component.module;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public interface Supplier extends PassiveModule {
    default List<ItemStack> supply() {
        Inventory inv = inventory();
        if (inv == null) return Arrays.asList();
        return Arrays.asList(Arrays.stream(inv.getContents()).filter(Objects::nonNull).toArray(ItemStack[]::new));
    }

    default boolean has(ItemStack stack) {
        return inventory().contains(stack, stack.getAmount());
    }

    int supplierPriority();

}
