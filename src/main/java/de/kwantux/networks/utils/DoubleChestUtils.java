package de.kwantux.networks.utils;

import de.kwantux.networks.component.BasicComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Chest;

import javax.annotation.Nullable;

import static de.kwantux.networks.Main.mgr;
import static de.kwantux.networks.component.BlockComponent.getComponentAtBlock;

public class DoubleChestUtils {

    @Nullable
    public BasicComponent componentAt(Origin origin) {
        BasicComponent component = mgr.getComponent(origin);
        if (component == null && origin instanceof BlockLocation pos) {
            if (!pos.isLoaded()) return null;
            if (pos.getBlock().getType().equals(Material.CHEST)) {
                Chest chest = (Chest) pos.getBlock().getBlockData();
                component = mgr.getComponent(shift(pos, chest.getType(), chest.getFacing()));
            }
        }
        return component;
    }

    @Nullable
    public BasicComponent componentAtLoadedBlock(Block block) {
        BasicComponent component = getComponentAtBlock(block);
        if (component == null) {
            if (block.getType().equals(Material.CHEST)) {
                Chest chest = (Chest) block.getBlockData();
                component = mgr.getComponent(shift(new BlockLocation(block), chest.getType(), chest.getFacing()));
            }
        }
        return component;
    }


    public void checkChest(Origin origin) {
        if (origin instanceof BlockLocation pos) { // Non-block components cannot be double chests

            Block block = pos.getBlock();
            if (!block.getType().equals(Material.CHEST) && !block.getType().equals(Material.TRAPPED_CHEST)) return;

            Chest chest = (Chest) block.getBlockData();
            if (chest.getType().equals(Chest.Type.SINGLE)) return;

            BasicComponent component = mgr.getComponent(pos);
            if (component == null) return;

            BasicComponent component2 = mgr.getComponent(shift(pos, chest.getType(), chest.getFacing()));
            if (component2 == null) return;

            chest.setType(Chest.Type.SINGLE);
            block.setBlockData(chest);
        }
    }

    public void disconnectChests(BlockLocation pos) {

        Block block = pos.getBlock();
        if (!block.getType().equals(Material.CHEST) && !block.getType().equals(Material.TRAPPED_CHEST)) return;

        Chest chest = (Chest) block.getBlockData();
        if (chest.getType().equals(Chest.Type.SINGLE)) return;

        BasicComponent component = mgr.getComponent(pos);
        if (component == null) return;

        Block block2 = (shift(pos, chest.getType(), chest.getFacing())).getBlock();
        Chest chest2 = (Chest) block2.getBlockData();

        chest.setType(Chest.Type.SINGLE);
        block.setBlockData(chest);

        chest2.setType(Chest.Type.SINGLE);
        block2.setBlockData(chest2);
    }

    private BlockLocation shift(BlockLocation location, Chest.Type type, BlockFace face) {
        if (type.equals(Chest.Type.LEFT)) {
            switch (face) {
                case NORTH:
                    return new BlockLocation(location.getX()+1, location.getY(), location.getZ(), location.getWorld());
                case WEST:
                    return new BlockLocation(location.getX(), location.getY(), location.getZ()-1, location.getWorld());
                case SOUTH:
                    return new BlockLocation(location.getX()-1, location.getY(), location.getZ(), location.getWorld());
                case EAST:
                    return new BlockLocation(location.getX(), location.getY(), location.getZ()+1, location.getWorld());
            }
        }
        if (type.equals(Chest.Type.RIGHT)) {
            switch (face) {
                case NORTH:
                    return new BlockLocation(location.getX()-1, location.getY(), location.getZ(), location.getWorld());
                case WEST:
                    return new BlockLocation(location.getX(), location.getY(), location.getZ()+1, location.getWorld());
                case SOUTH:
                    return new BlockLocation(location.getX()+1, location.getY(), location.getZ(), location.getWorld());
                case EAST:
                    return new BlockLocation(location.getX(), location.getY(), location.getZ()-1, location.getWorld());
            }
        }
        return location;
    }
}
