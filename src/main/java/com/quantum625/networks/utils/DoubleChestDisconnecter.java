package com.quantum625.networks.utils;

import com.quantum625.networks.NetworkManager;
import com.quantum625.networks.component.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Chest;

public class DoubleChestDisconnecter {

    private NetworkManager net;

    public DoubleChestDisconnecter(NetworkManager net) {
        this.net = net;
    }

    // Returns true if the chest is able to be a double chest
    // Returns false if the chest is not a double chest
    public void checkChest(Location pos) {

        Bukkit.getLogger().info("Checking chest at "+ pos.toString());

        Block block = pos.getBlock();

        if (!block.getType().equals(Material.CHEST)) return;

        Bukkit.getLogger().info("Block is a chest");

        Chest chest = (Chest) block.getBlockData();

        if (chest.getType().equals(Chest.Type.SINGLE)) return;

        Bukkit.getLogger().info("Chest is a double chest");

        BaseComponent component = net.getComponentByLocation(pos);

        if (component == null) return;

        Bukkit.getLogger().info("Pos1 is a component");

        BaseComponent component2 = net.getComponentByLocation(shift(pos, chest.getType(), chest.getFacing()));

        if (component2 == null) return;

        Bukkit.getLogger().info("Pos2 is a component");


        if (!component.getType().equals(component2.getType())) {
            Bukkit.getLogger().info("Invalid component next to each other");
            chest.setType(Chest.Type.SINGLE);
            block.setBlockData(chest);
        }

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
