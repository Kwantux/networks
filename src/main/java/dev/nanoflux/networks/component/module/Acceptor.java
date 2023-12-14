package dev.nanoflux.networks.component.module;

import org.bukkit.inventory.ItemStack;

public interface Acceptor extends BaseModule {
    default boolean accept(ItemStack stack) {
        return inventory().firstEmpty() != -1;
    }

    int acceptorPriority();
}
