package de.kwantux.config;

import de.kwantux.config.util.Transformation;
import de.kwantux.config.util.exceptions.InvalidNodeException;
import org.apache.commons.lang3.SerializationException;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public abstract class RawConfiguration {

    protected JavaPlugin plugin;
    protected Logger logger = Bukkit.getLogger();
    protected String path;
    protected List<String> requirements = new ArrayList<String>();
    protected List<Transformation> transformations = new ArrayList<Transformation>();


    protected abstract void update();

    /**
     * Resolves all transformations
     * (When the config syntax is changed in an update, that it will be automatically converted to the new one)
     */
    protected void resolveTransformations(ComparableVersion oldVersion, ComparableVersion newVersion) {
        for (Transformation transformation : transformations) {
            if (
                    (transformation.minVersion() != null && oldVersion.compareTo(transformation.minVersion()) < 0) ||
                            (transformation.maxVersion() != null && oldVersion.compareTo(transformation.maxVersion()) > 0)
            ) continue;
            try {
                ConfigurationNode node = get(transformation.oldKey());
                if (node.isNull()) continue;
                if (transformation.transform() != null)
                    node = transformation.transform().apply(node);
                if (transformation.newKey() != null) set(transformation.newKey(), node);
                if (transformation.delete() && node.parent() != null)
                    if (node.parent().isMap()) node.parent().removeChild(node.key());
            } catch (InvalidNodeException ignored) {
                // nothing to do if the old value doesn't exist anymore :)
            }
        }
    }

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

    public void require(String... paths) {
        requirements.addAll(Arrays.stream(paths).toList());
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

    /**
     * Tests if all the required options are set
     * @return True if all the required options are not null
     */
    public boolean silentTestRequirements() {
        boolean result = true;
        for (String req : requirements) {
            if (!has(req))  {
                result = false;
            }
        }
        return result;
    }


    public abstract ConfigurationNode get(String path) throws InvalidNodeException;
    public abstract void set(String path, Object value) throws SerializationException;

    public String getPath() {
        return path;
    }
    public JavaPlugin getPlugin() {
        return plugin;
    }

    public abstract boolean has(String s);

    public void transformation(Transformation transformation) {
        transformations.add(transformation);
    }
}
