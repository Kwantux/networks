package net.quantum625.config;

import net.quantum625.config.util.exceptions.InvalidNodeException;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.logging.Logger;

public abstract class RawConfiguration {

    protected JavaPlugin plugin;
    protected Logger logger;
    protected String path;
    protected boolean ingameEdit = false;


    protected abstract void update();


    public abstract void reload();

    public abstract void save();


    public abstract void enableIngameChange();

    public abstract void disableIngameChange();
    public abstract boolean ingameChangeEnabled();


    //abstract ConfigurationNode get(String path) throws InvalidNodeException;
    //public abstract void set(String path, Object value) throws SerializationException;

    public String getPath() {
        return path;
    }
    public JavaPlugin getPlugin() {
        return plugin;
    }

    public abstract boolean has(String s);
}
