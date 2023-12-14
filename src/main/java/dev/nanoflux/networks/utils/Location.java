package dev.nanoflux.networks.utils;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.util.UUID;


public class Location {
    private int x;
    private int y;
    private int z;

    private UUID world;


    public Location(int x, int y, int z, UUID world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public Location(org.bukkit.Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = location.getWorld().getUID();
    }

    public Location(org.bukkit.block.Block block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.world = block.getWorld().getUID();
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setWorld(UUID world) {
        this.world = world;
    }


    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getZ() {
        return z;
    }
    public UUID getWorld() {
        return world;
    }

    public org.bukkit.Location getBukkitLocation() {
        return new org.bukkit.Location(Bukkit.getWorld(getWorld()), x, y, z);
    }

    public Block getBlock() {
        return this.getBukkitLocation().getBlock();
    }

    @Override
    public String toString() {
        return "[" + world + ", " + x + ", " + y + ", " + z + "]";
    }


    public boolean equals(Location other) {
        return (other.getX() == this.getX() && other.getY() == this.getY() && other.getZ() == this.getZ() && other.getWorld().equals(this.getWorld()));
    }

    public double getDistance(Location second) {
        if (!second.getWorld().equals(this.getWorld())) {
            return 2147483647;
        }
        return Math.sqrt(Math.pow(this.x-second.getX(),2)+Math.pow(this.y-second.getY(),2)+Math.pow(this.z-second.getZ(),2));
    }
}
