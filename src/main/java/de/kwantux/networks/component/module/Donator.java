package de.kwantux.networks.component.module;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Donator extends ActiveModule {
    List<ItemStack> donate();
}
