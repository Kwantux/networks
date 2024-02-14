package dev.nanoflux.networks.component.module;

import dev.nanoflux.networks.utils.BlockLocation;
import org.bukkit.inventory.Inventory;

public interface BaseModule {
    BlockLocation pos();
    Inventory inventory();
}
