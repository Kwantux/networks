package com.quantum625.autosort.utils;

public class Location {
    private int x = 0;
    private int y;
    private int z;

    private String dim;


    public Location(int x, int y, int z, String dim) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;

    }

    public Location(org.bukkit.Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.dim = location.getWorld().getName();
    }

    public Location(org.bukkit.block.Block block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.dim = block.getWorld().getName();
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

    public void setDim(String dim) {
        this.dim = dim;
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
    public String getDim() {
        return dim;
    }

    public String toString() {
        return "[" + dim + ", " + x + ", " + y + ", " + z + "]";
    }
}
