package de.kwantux.networks.component;


import de.kwantux.networks.component.util.ComponentType;
import de.kwantux.networks.config.Config;
import de.kwantux.networks.utils.BlockLocation;
import de.kwantux.networks.utils.Origin;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


public abstract class BlockComponent extends InstallableComponent {

    public abstract ComponentType type();

    protected BlockLocation pos;

    protected BlockComponent(BlockLocation pos) {
        this.pos = pos;
    }

    public BlockLocation pos() {
        return pos;
    }
    public Origin origin() {
        return pos;
    }

    public boolean isLoaded() {
        return pos.isLoaded();
    }

    public boolean ready() {
        return Config.loadChunks || isLoaded();
    }

    public @Nullable Inventory inventory() {

        if (!ready()) return null;

        Block block = Objects.requireNonNull(Bukkit.getWorld(pos.getWorld())).getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        if (block.getState() instanceof InventoryHolder) {
            return ((InventoryHolder) block.getState()).getInventory();
        }
        // TODO: Remove Component from database
        return null;
    }
}
