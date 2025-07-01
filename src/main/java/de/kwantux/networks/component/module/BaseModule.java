package de.kwantux.networks.component.module;

import de.kwantux.networks.utils.BlockLocation;
import de.kwantux.networks.utils.Origin;
import org.bukkit.inventory.Inventory;

public interface BaseModule {
    BlockLocation pos();
    Origin origin();
    boolean isLoaded();
    boolean ready();
    Inventory inventory();
}
