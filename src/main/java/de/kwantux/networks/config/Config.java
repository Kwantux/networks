package de.kwantux.networks.config;

import de.kwantux.config.SimpleConfig;
import de.kwantux.networks.Main;
import de.kwantux.networks.component.util.ComponentType;
import de.kwantux.networks.storage.NetworkProperties;
import de.kwantux.networks.utils.BlockLocation;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Config {

    Logger logger;

    SimpleConfig config;

    public Config(Main main) {
        logger = main.getLogger();
        this.config = new SimpleConfig(main, "general.conf");

        // Define all default values with comments
        defineDefaults();
        config.load();

        // Load values from config
        blastProofComponents = config.getBoolean("blastProofComponents");
        noticeEnabled = config.getBoolean("notice");
        requestOwnershipTransfers = config.getBoolean("requestOwnershipTransfers");
        complexInventoryChecks = config.getBoolean("performance.complexInventoryChecks");
        loadChunks = config.getBoolean("performance.loadChunks");
        autoSaveInterval = config.getInt("autoSave");
        commands = config.getStringArray("commands");
        ranges = config.getIntArray("range");
        if (ranges[0] == 0) {
            int baseRange = config.getInt("properties.baseRange");
            for (int i = 0; i < ranges.length; i++) {
                if (ranges[i] >= 0) ranges[i] += baseRange;
            }
            config.set("range", ranges);
            config.unset("properties.baseRange");
        }
        rangePerNetwork = config.getBoolean("rangePerNetwork");
        maxNetworks = config.getInt("maxNetworks");
        allowMerge = config.getBoolean("allowMerge");
        resetContainerNamesOnDelete = config.getBoolean("resetContainerNamesOnDelete");

        wandMaterial = getWandMaterial();
        componentUpgradeMaterial = getComponentUpgradeMaterial();
        rangeUpgradeMaterial = getRangeUpgradeMaterial();
    }

    /**
     * Define all default configuration values with comments
     */
    private void defineDefaults() {
        // Language settings
        config.defineDefault("lang", "en", "The language file in /lang that is used. Do not add the .yml, only the language id");
        
        // Command aliases
        config.defineDefault("commands", new String[]{"n", "net", "network"}, "Command aliases - Alternative /-commands for /networks");

        String[] componentBlockWhitelist = new String[]{"CHEST", "TRAPPED_CHEST", "BARREL", "HOPPER", "DISPENSER", "DROPPER", "SHULKER_BOX", "WHITE_SHULKER_BOX", "ORANGE_SHULKER_BOX", "MAGENTA_SHULKER_BOX", "LIGHT_BLUE_SHULKER_BOX", "YELLOW_SHULKER_BOX", "LIME_SHULKER_BOX", "PINK_SHULKER_BOX", "GRAY_SHULKER_BOX", "LIGHT_GRAY_SHULKER_BOX", "CYAN_SHULKER_BOX", "PURPLE_SHULKER_BOX", "BLUE_SHULKER_BOX", "BROWN_SHULKER_BOX", "GREEN_SHULKER_BOX", "RED_SHULKER_BOX", "BLACK_SHULKER_BOX", "COPPER_CHEST", "EXPOSED_COPPER_CHEST", "WEATHERED_COPPER_CHEST", "OXIDIZED_COPPER_CHEST", "WAXED_COPPER_CHEST", "WAXED_EXPOSED_COPPER_CHEST", "WAXED_WEATHERED_COPPER_CHEST", "WAXED_OXIDIZED_COPPER_CHEST"};

        // Component blocks
        config.defineDefault("component.input", componentBlockWhitelist, "Blocks that are allowed to be network input containers. Only works for blocks that have inventories.");
        config.defineDefault("component.sorted", componentBlockWhitelist, "Blocks that are allowed to be network sorted containers. Only works for blocks that have inventories.");
        config.defineDefault("component.fallback", componentBlockWhitelist, "Blocks that are allowed to be network fallback containers. Only works for blocks that have inventories.");
        
        // Auto-save
        config.defineDefault("autoSave", 30, "Auto-save interval in seconds. Set to 0 to disable auto save.");
        
        // Performance settings
        config.defineDefault("performance.complexInventoryChecks", false, "This option is experimental, use with caution. FALSE: Only checks if said inventory has a free slot (better for performance) [DEFAULT]. TRUE: Checks whether the stack that's to fill in can be spread to partially filled slots with the same item type");
        config.defineDefault("performance.loadChunks", false, "Load chunks of connected network components. Guarantees that items can be transmitted as far as you want. May SIGNIFICANTLY reduce your server's performance on LARGE servers! Use with caution. On Folia servers this does not always work since item transmission must happen within one tick region");
        config.defineDefault("resetContainerNamesOnDelete", true, "When deleting a network, reset the names of all containers in the network to their default names. This means loading all chunks with containers in them.");
        config.defineDefault("allowMerge", true, "Allow merging networks. This means loading all chunks with containers of that network in them.");

        // Materials
        config.defineDefault("material.wand", "BLAZE_ROD", "Range upgrade base material");
        config.defineDefault("material.range", "LIGHTNING_ROD", "Range upgrade material");
        config.defineDefault("material.component", "ITEM_FRAME", "Component upgrade material");
        
        // Network properties
        config.defineDefault("properties.baseRange", 0, "Deprecated: Default base range for networks. Will be removed in a future version. Use range array instead.");
        config.hideByDefault("properties.baseRange");
        // Feature toggles
        config.defineDefault("blastProofComponents", true, "Disables explosion damage on network components");
        config.defineDefault("notice", true, "Notices players that open full chests to this plugin. This message will only be sent once and just to players that do not have a network. You can configure the message in the language file");
        config.defineDefault("logoOnLaunch", true, "Show Networks text on launch");
        
        // Range upgrades
        config.defineDefault("range", new Integer[]{25, 50, 100, 200, 500, -1}, "Defines the maximum range items can be teleported for each level of range. This value is added to the base range property of the network. You can add more range upgrades to this list, but you NEED to also give them a crafting recipe. Paste 2147483647 to enable infinite item transmission. Paste -1 to enable interdimensional item transmission. Values must be integers between -1 and 2147483647");
        config.defineDefault("rangePerNetwork", false, "Whether range upgrades are per component or per network");
        
        // Player limits
        config.defineDefault("maxNetworks", 20, "The maximum amount of networks a player can own");
        config.defineDefault("requestOwnershipTransfers", true, "Whether players should need to accept network ownership transfers");
    }

    public static boolean blastProofComponents;
    /**
     * Whether players that don't have a network should get a notification when opening a full chest
     */
    public static boolean noticeEnabled;
    public static boolean requestOwnershipTransfers;
    public static boolean complexInventoryChecks;
    public static boolean loadChunks;
    public static boolean rangePerNetwork;
    public static boolean allowMerge;
    public static boolean resetContainerNamesOnDelete;
    public static int maxNetworks;
    /**
     * Auto save interval in seconds
     */
    public static int autoSaveInterval;
    public static String[] commands;
    public static Integer[] ranges;
    public static Material wandMaterial;
    public static Material rangeUpgradeMaterial;
    public static Material componentUpgradeMaterial;


    public void setLanguage(String language) {
        config.set("lang", language);
    }
    public String getLanguage() {
        return config.getString("lang");
    }



    public List<Material> componentBlocks(ComponentType componentType) {
        List<Material> allowed = new ArrayList<>();
        String[] materials = config.getStringArray("component."+componentType.tag);
        for (String mat : materials) {
            allowed.add(Material.getMaterial(mat));
        }
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

    public NetworkProperties defaultProperties() {
        return new NetworkProperties(
                config.getInt("properties.baseRange")
        );
    }

    private Material getComponentUpgradeMaterial() {
        try {
            return Material.getMaterial(config.getString("material.component"));
        } catch (EnumConstantNotPresentException e) {
            logger.severe(config.getString("material.component") + " is not a valid material, please change your config!");
        }
        return Material.ITEM_FRAME;
    }

    private Material getRangeUpgradeMaterial() {
        try {
            return Material.getMaterial(config.getString("material.range"));
        } catch (EnumConstantNotPresentException e) {
            logger.severe(config.getString("material.range") + " is not a valid material, please change your config!");
        }
        return Material.LIGHTNING_ROD;
    }

    private Material getWandMaterial() {
        try {
            return Material.getMaterial(config.getString("material.wand"));
        } catch (EnumConstantNotPresentException e) {
            logger.severe(config.getString("material.wand") + " is not a valid material, please change your config!");
        }
        return Material.LIGHTNING_ROD;
    }


    public boolean logoOnLaunch() {
        return config.getBoolean("logoOnLaunch");
    }

    public void reload() {
        config.load();
    }
}
