package de.kwantux.networks.component.module;

import org.bukkit.inventory.ItemStack;

public interface Acceptor extends PassiveModule {
    boolean accept(ItemStack stack);

    int acceptorPriority();

    void incrementAcceptorPriority();
    void decrementAcceptorPriority();
}
