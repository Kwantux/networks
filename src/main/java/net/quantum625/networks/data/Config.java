package net.quantum625.networks.data;

import net.quantum625.config.Configuration;
import net.quantum625.config.util.exceptions.InvalidNodeException;
import net.quantum625.networks.Main;
import net.quantum625.networks.utils.Location;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Config {

    Logger logger;

    Configuration config;
    Configuration features;

    public Config(Main main) throws SerializationException {
        logger = main.getLogger();
        this.config = Configuration.createMain(main, "networks.conf");
        this.features = Configuration.createMain(main, "features.conf");
    }

    public boolean blastProofComponents() {return config.getFinalBoolean("blastProofComponents");}

    public boolean updateAllowed() {return config.getFinalBoolean("autoUpdate");}
    public boolean noticeEnabled() {return config.getFinalBoolean("notice");}

    public void setLanguage(String language) {
        config.set("lang", language);
    }
    public String getLanguage() {
        return config.getFinalString("lang").toString();
    }



    public Integer get(String id) {
        return config.getFinalInt(id);
    }

    public List<String> getContainerWhitelist() {
        return config.getFinalList("containerWhitelist", String.class);
    }
    public boolean checkLocation(Location location, String component) {
        List<String> whitelist = config.getFinalList("containerWhitelist", String.class);
        for (String e : whitelist) {
            if (e.equalsIgnoreCase(location.getBukkitLocation().getBlock().getType().toString().toUpperCase())) {
                return true;
            }
        }
        return false;
    }


    public Integer[] getMaxRanges() {
        try {
            return config.getList("range", Integer.class).toArray(new Integer[0]);
        } catch (InvalidNodeException e) {
            throw new RuntimeException(e);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }


    private @Nullable String getString(String path) {
        try {
            return config.getString(path);
        }
        catch (InvalidNodeException e) {
            logger.severe("");
        }
        return null;
    }
}
