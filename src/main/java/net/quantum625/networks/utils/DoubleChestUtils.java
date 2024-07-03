package net.quantum625.networks.utils;

import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.component.BaseComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Chest;

import java.util.List;

public class DoubleChestUtils {

    private final NetworkManager net;

    private static final List<Material> CHESTS = List.of(Material.CHEST, Material.TRAPPED_CHEST);

    public DoubleChestUtils(NetworkManager net) {
        this.net = net;
    }

    public BaseComponent componentAt(Location pos) {
        BaseComponent component = net.getComponentByLocation(pos);
        if (component == null && CHESTS.contains(pos.getBlock().getType())) {
            Chest chest = (Chest) pos.getBlock().getBlockData();
            component = net.getComponentByLocation(shift(pos, chest.getType(), chest.getFacing()));
        }
        return component;
    }

    public void checkChest(Location pos) {

        Block block = pos.getBlock();

        if (!block.getType().equals(Material.CHEST) && !block.getType().equals(Material.TRAPPED_CHEST)) return;

        Chest chest = (Chest) block.getBlockData();

        if (chest.getType().equals(Chest.Type.SINGLE)) return;

        BaseComponent component = net.getComponentByLocation(pos);

        if (component == null) return;

        BaseComponent component2 = net.getComponentByLocation(shift(pos, chest.getType(), chest.getFacing()));

        if (component2 == null) return;


        chest.setType(Chest.Type.SINGLE);
        block.setBlockData(chest);

    }

    public void disconnectChests(Location pos) {

        Block block = pos.getBlock();

        if (!block.getType().equals(Material.CHEST) && !block.getType().equals(Material.TRAPPED_CHEST)) return;

        Chest chest = (Chest) block.getBlockData();

        if (chest.getType().equals(Chest.Type.SINGLE)) return;

        BaseComponent component = net.getComponentByLocation(pos);

        if (component == null) return;

        Block block2 = (shift(pos, chest.getType(), chest.getFacing())).getBlock();
        Chest chest2 = (Chest) block2.getBlockData();

        chest.setType(Chest.Type.SINGLE);
        block.setBlockData(chest);

        chest2.setType(Chest.Type.SINGLE);
        block2.setBlockData(chest2);

    }

    private Location shift(Location location, Chest.Type type, BlockFace face) {
        if (type.equals(Chest.Type.LEFT)) {
            switch (face) {
                case NORTH:
                    return new Location(location.getX()+1, location.getY(), location.getZ(), location.getDim());
                case WEST:
                    return new Location(location.getX(), location.getY(), location.getZ()-1, location.getDim());
                case SOUTH:
                    return new Location(location.getX()-1, location.getY(), location.getZ(), location.getDim());
                case EAST:
                    return new Location(location.getX(), location.getY(), location.getZ()+1, location.getDim());
            }
        }
        if (type.equals(Chest.Type.RIGHT)) {
            switch (face) {
                case NORTH:
                    return new Location(location.getX()-1, location.getY(), location.getZ(), location.getDim());
                case WEST:
                    return new Location(location.getX(), location.getY(), location.getZ()+1, location.getDim());
                case SOUTH:
                    return new Location(location.getX()+1, location.getY(), location.getZ(), location.getDim());
                case EAST:
                    return new Location(location.getX(), location.getY(), location.getZ()-1, location.getDim());
            }
        }
        return null;
    }
}
