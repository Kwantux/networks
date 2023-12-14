package dev.nanoflux.networks.component.module;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public interface Supplier extends BaseModule {
    default List<ItemStack> supply() {
        return Arrays.asList(inventory().getContents());
    }

    default boolean has(ItemStack stack) {
        return inventory().contains(stack, stack.getAmount());
    }

    int supplierPriority();

}
