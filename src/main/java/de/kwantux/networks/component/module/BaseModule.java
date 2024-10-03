package de.kwantux.networks.component.module;

import de.kwantux.networks.utils.BlockLocation;
import org.bukkit.inventory.Inventory;

public interface BaseModule {
    BlockLocation pos();
    boolean isLoaded();
    boolean ready();
    Inventory inventory();
}
