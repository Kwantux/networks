package dev.nanoflux.networks;

import dev.nanoflux.config.Configuration;
import dev.nanoflux.config.ConfigurationManager;
import dev.nanoflux.config.util.Transformation;
import dev.nanoflux.config.util.exceptions.ConfigAlreadyRegisteredException;
import dev.nanoflux.config.util.exceptions.InvalidNodeException;
import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.storage.NetworkProperties;
import dev.nanoflux.networks.utils.BlockLocation;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Config {

    Logger logger;

    Configuration config;

    public Config(Main main) throws SerializationException {
        logger = main.getLogger();
        try {
            this.config = Configuration.createMain(main, "general.conf");

            config.require("blastProofComponents");
            config.require("notice");
            config.require("lang");
            config.require("range");
            config.require("maxNetworks");
            config.require("properties.baseRange", "properties.maxComponents", "properties.maxUsers");
            config.require("component.input", "component.sorting", "component.misc");
            config.require("material.component");
            config.require("material.range");
            config.require("performance.complexInventoryChecks", "performance.loadChunks");
            config.require("logoOnLaunch");
            config.require("requestOwnershipTransfers");
            config.require("humanReadableJson");
            config.require("archiveNetworksOnDelete");

            // Legacy config support
            config.transformation(
                    new Transformation(
                            new ComparableVersion("2.0.0"),
                            new ComparableVersion("2.1.9"),
                            "containerWhitelist",
                            "component.input",
                            false,
                            null
                    )
            );
            config.transformation(
                    new Transformation(
                            new ComparableVersion("2.0.0"),
                            new ComparableVersion("2.1.9"),
                            "containerWhitelist",
                            "component.sorting",
                            false,
                            null
                    )
            );
            config.transformation(
                    new Transformation(
                            new ComparableVersion("2.0.0"),
                            new ComparableVersion("2.1.9"),
                            "containerWhitelist",
                            "component.misc",
                            true,
                            null
                    )
            );

            config.update();

            blastProofComponents = config.getFinalBoolean("blastProofComponents");
            noticeEnabled = config.getFinalBoolean("notice");
            humanReadableJson = config.getFinalBoolean("humanReadableJson");
            archiveNetworksOnDelete = config.getFinalBoolean("archiveNetworksOnDelete");
            requestOwnershipTransfers = config.getFinalBoolean("requestOwnershipTransfers");
            complexInventoryChecks = config.getFinalBoolean("performance.complexInventoryChecks");
            loadChunks = config.getFinalBoolean("performance.loadChunks");

        } catch (ConfigAlreadyRegisteredException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean blastProofComponents; // TODO: Blast proof components
    public static boolean noticeEnabled; // TODO: Notice message
    public static boolean humanReadableJson;
    public static boolean archiveNetworksOnDelete;
    public static boolean requestOwnershipTransfers;
    public static boolean complexInventoryChecks;
    public static boolean loadChunks;


    public void setLanguage(String language) {
        config.set("lang", language);
    }
    public String getLanguage() {
        return config.getFinalString("lang");
    }



    public List<Material> componentBlocks(ComponentType componentType) {
        List<Material> allowed = new ArrayList<>();
        config.getFinalList("component."+componentType.tag, String.class).forEach(mat -> allowed.add(Material.getMaterial(mat)));
        return allowed;
    }
    public boolean checkLocation(BlockLocation location, ComponentType componentType) {
        List<Material> whitelist = componentBlocks(componentType);
        for (Material mat : whitelist) {
            if (location.getBukkitLocation().getBlock().getType().equals(mat)) {
                return true;
            }
        }
        return false;
    }

    public int getMaxNetworks() {
        return config.getFinalInt("maxNetworks");
    }

    public NetworkProperties defaultProperties() {
        return new NetworkProperties(
                config.getFinalInt("properties.baseRange"),
                config.getFinalInt("properties.maxComponents"),
                config.getFinalInt("properties.maxUsers")
        );
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
