package de.kwantux.networks.component.module;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Requestor extends ActiveModule {
    List<ItemStack> requested();
}
