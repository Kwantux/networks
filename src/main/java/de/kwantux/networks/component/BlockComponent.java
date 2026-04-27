package de.kwantux.networks.component;


import de.kwantux.networks.Main;
import de.kwantux.networks.Network;
import de.kwantux.networks.component.util.ComponentType;
import de.kwantux.networks.config.Config;
import de.kwantux.networks.utils.BlockLocation;
import de.kwantux.networks.utils.Origin;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static de.kwantux.networks.Main.mgr;
import static de.kwantux.networks.utils.NamespaceUtils.BLOCK_DATA_KEY;


public abstract class BlockComponent extends InstallableComponent {

    public abstract ComponentType type();

    protected BlockLocation pos;

    protected BlockComponent(BlockLocation pos, Network network) {
        this.pos = pos;
        this.network = network;

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

    @Override
    public void addStorageEntry(Network network) {
        pos.getBlock().setMetadata(BLOCK_DATA_KEY, new FixedMetadataValue(Main.instance, network.name()));
    }

    @Override
    public void removeStorageEntry() {
        pos.getBlock().removeMetadata(BLOCK_DATA_KEY, Main.instance);
    }

    public static @Nullable BasicComponent getComponentAtBlock(Block block) {
        for (MetadataValue value : block.getMetadata(BLOCK_DATA_KEY)) {
            return mgr.getNetwork(value.asString()).getComponent(new BlockLocation(block));
        }
        return null;
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
