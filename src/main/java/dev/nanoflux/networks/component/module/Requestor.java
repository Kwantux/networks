package dev.nanoflux.networks.component.module;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Requestor extends BaseModule {
    List<ItemStack> requested();

}
