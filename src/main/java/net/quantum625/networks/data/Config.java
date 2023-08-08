package net.quantum625.networks.data;

import net.quantum625.config.Configuration;
import net.quantum625.config.ConfigurationManager;
import net.quantum625.config.util.exceptions.ConfigAlreadyRegisteredException;
import net.quantum625.config.util.exceptions.InvalidNodeException;
import net.quantum625.networks.Main;
import net.quantum625.networks.utils.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class Config {

    Logger logger;

    Configuration config;
    Configuration features;

    public Config(Main main) throws SerializationException {
        logger = main.getLogger();
        try {
            this.config = Configuration.createMain(main, "networks.conf");
            this.features = Configuration.create(main, "features", "features.conf");

            config.require("blastProofComponents");
            config.require("autoUpdate");
            config.require("notice");
            config.require("lang");
            config.require("containerWhitelist");
            config.require("range");
            config.require("material.component");
            config.require("material.range");
            config.require("logStartupInformation");
            config.require("logoOnLaunch");

        } catch (ConfigAlreadyRegisteredException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean blastProofComponents() {return config.getFinalBoolean("blastProofComponents");}

    public boolean updateAllowed() {return config.getFinalBoolean("autoUpdate");}
    public boolean noticeEnabled() {return config.getFinalBoolean("notice");}

    public void setLanguage(String language) {
        config.set("lang", language);
    }
    public String getLanguage() {
        return config.getFinalString("lang");
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

    public Material getComponentUpgradeMaterial() {
        try {
            return Material.getMaterial(getString("material.component"));
        } catch (EnumConstantNotPresentException e) {
            logger.severe(getString("material.component") + " is not a valid material, please change your config!");
        }
        return Material.ITEM_FRAME;
    }

    public Material getRangeUpgradeMaterial() {
        try {
            return Material.getMaterial(getString("material.range"));
        } catch (EnumConstantNotPresentException e) {
            logger.severe(getString("material.range") + " is not a valid material, please change your config!");
        }
        return Material.LIGHTNING_ROD;
    }


    public boolean logoOnLaunch() {
        return Boolean.TRUE.equals(config.getFinalBoolean("logoOnLaunch"));
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

    public void reload() {
        config.reload();
        ConfigurationManager.saveAll();
        ConfigurationManager.reloadAll();
    }
}
