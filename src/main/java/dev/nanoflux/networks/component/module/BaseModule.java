package dev.nanoflux.networks.component.module;

import dev.nanoflux.networks.Config;
import dev.nanoflux.networks.utils.BlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;

public interface BaseModule {
    BlockLocation pos();
    boolean isLoaded();
    boolean ready();
    Inventory inventory();
}
