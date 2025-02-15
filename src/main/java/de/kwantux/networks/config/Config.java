package de.kwantux.networks.config;

import de.kwantux.config.Configuration;
import de.kwantux.config.ConfigurationManager;
import de.kwantux.config.util.exceptions.ConfigAlreadyRegisteredException;
import de.kwantux.config.util.exceptions.InvalidNodeException;
import de.kwantux.networks.Main;
import de.kwantux.networks.compat.ConfigurationTransformers;
import de.kwantux.networks.component.ComponentType;
import de.kwantux.networks.storage.NetworkProperties;
import de.kwantux.networks.utils.BlockLocation;
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
            config.require("commands");
            config.require("notice");
            config.require("lang");
            config.require("range");
            config.require("maxNetworks");
            config.require("propertyLore");
            config.require("properties.baseRange", "properties.maxComponents", "properties.maxUsers");
            config.require("component.input", "component.sorting", "component.misc");
            config.require("material.component");
            config.require("material.range");
            config.require("performance.complexInventoryChecks", "performance.loadChunks");
            config.require("logoOnLaunch");
            config.require("requestOwnershipTransfers");
            config.require("humanReadableJson");
            config.require("archiveNetworksOnDelete");
            config.require("autoSave");

            ConfigurationTransformers.generalConfigTransformers(config);

            config.update();

            blastProofComponents = config.getFinalBoolean("blastProofComponents");
            noticeEnabled = config.getFinalBoolean("notice");
            humanReadableJson = config.getFinalBoolean("humanReadableJson");
            archiveNetworksOnDelete = config.getFinalBoolean("archiveNetworksOnDelete");
            requestOwnershipTransfers = config.getFinalBoolean("requestOwnershipTransfers");
            complexInventoryChecks = config.getFinalBoolean("performance.complexInventoryChecks");
            propertyLore = config.getFinalBoolean("propertyLore");
            loadChunks = config.getFinalBoolean("performance.loadChunks");
            autoSaveInterval = config.getFinalInt("autoSave");
            commands = config.getFinalList("commands", String.class).toArray(new String[0]);
            ranges = config.getList("range", Integer.class).toArray(new Integer[0]);

        } catch (ConfigAlreadyRegisteredException | InvalidNodeException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean blastProofComponents;
    /**
     * Whether players that don't have a network should get a notification when opening a full chest
     */
    public static boolean noticeEnabled;
    public static boolean humanReadableJson;
    public static boolean archiveNetworksOnDelete;
    public static boolean requestOwnershipTransfers;
    public static boolean complexInventoryChecks;
    public static boolean propertyLore;
    public static boolean loadChunks;
    /**
     * Auto save interval in seconds
     */
    public static int autoSaveInterval;
    public static String[] commands;
    public static Integer[] ranges;


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
