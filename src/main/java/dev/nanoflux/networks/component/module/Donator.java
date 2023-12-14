package dev.nanoflux.networks.component.module;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Donator extends BaseModule {
    List<ItemStack> donate();
}
