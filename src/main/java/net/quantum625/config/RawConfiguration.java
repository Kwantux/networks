package net.quantum625.config;

import net.quantum625.config.util.exceptions.InvalidNodeException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class RawConfiguration {

    protected JavaPlugin plugin;
    protected Logger logger = Bukkit.getLogger();
    protected String path;
    protected boolean ingameEdit = false;
    protected List<String> requirements = new ArrayList<String>();


    protected abstract void update();


    public abstract void reload();

    public abstract void save();


    /**
     * Adds a path to the list of required options
     * @param path The path of the option, that is required
     */
    public void require(String path) {
        if (!requirements.contains(path)) requirements.add(path);
    }

    /**
     * Adds multiple paths to the list of required options
     * @param paths The paths of the options, that are required
     */

    public void require(List<String> paths) {
        requirements.addAll(paths);
    }

    /**
     * Removes a path from the list of required options
     * @param path The path of the option, that was required
     *
     */
    public void unrequire(String path) {
        requirements.remove(path);
    }


    /**
     * Tests if all the required options are set
     * @return True if all the required options are not null
     */
    public boolean testRequirements() {
        boolean result = true;
        for (String req : requirements) {
            if (!has(req))  {
                logger.severe("[QC] Configuration file '"+this.path+"' is missing option: " + req);
                result = false;
            }
        }
        return result;
    }


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
