package de.kwantux.networks.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import java.util.UUID;

import static de.kwantux.networks.Main.lang;


public class BlockLocation {
    private int x;
    private int y;
    private int z;

    private UUID world;


    public BlockLocation(int x, int y, int z, UUID world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public BlockLocation(@Nonnull org.bukkit.Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = location.getWorld().getUID();
    }

    public BlockLocation(@Nonnull org.bukkit.block.Block block) {
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
        return "[" + x + ", " + y + ", " + z + "]";
    }

    public Component displayText() {
        return Component.text(toString()).clickEvent(ClickEvent.runCommand("/tpw " + world.toString() + " " + x + " " + y + " " + z)).hoverEvent(HoverEvent.showText(lang.getFinal("click-to-tp")));
    }


    @Override
    public boolean equals(Object otherObject) {
        if (otherObject instanceof BlockLocation other) {
            return (other.getX() == this.getX() && other.getY() == this.getY() && other.getZ() == this.getZ() && other.getWorld().equals(this.getWorld()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (world.toString() + "," + x + "," + y + "," + z).hashCode();
    }

    public double getDistance(BlockLocation second) {
        if (!second.getWorld().equals(this.getWorld())) {
            return Integer.MAX_VALUE;
        }
        return Math.sqrt(Math.pow(this.x-second.getX(),2)+Math.pow(this.y-second.getY(),2)+Math.pow(this.z-second.getZ(),2));
    }
}
