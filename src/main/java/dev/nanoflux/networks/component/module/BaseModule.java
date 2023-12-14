package dev.nanoflux.networks.component.module;

import dev.nanoflux.networks.utils.Location;
import org.bukkit.inventory.Inventory;

public interface BaseModule {
    Location pos();
    Inventory inventory();
}
