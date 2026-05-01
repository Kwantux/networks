package de.kwantux.networks.config;

import de.kwantux.config.SimpleConfig;
import de.kwantux.config.lang.LanguageController;
import de.kwantux.networks.Main;
import de.kwantux.networks.component.util.ComponentType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static de.kwantux.networks.config.Config.*;

public class CraftingManager {

    private final Main plugin;
    private final SimpleConfig config;
    private final Logger logger;
    private final LanguageController lang;

    private final Config pluginconfig;

    public static List<NamespacedKey> recipes = new ArrayList<>();



    public void save() {
        config.saveConfig();
    }


    private final char[] keys = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};


    public ItemStack getNetworkWand(int mode) {
        ItemStack wand = new ItemStack(wandMaterial);
        ItemMeta meta = wand.getItemMeta();
        try {
            meta.displayName(lang.getItemName("wand"+mode));
            meta.lore(lang.getItemLore("wand"+mode));
        } catch (de.kwantux.config.util.exceptions.InvalidNodeException e) {
            logger.warning("Missing language key for wand" + mode);
        }
        setCustomModelDataForWand(meta, mode);
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "wand"), PersistentDataType.INTEGER, mode);
        wand.setItemMeta(meta);
        return wand;
    }

    public ItemStack getRangeUpgrade(int tier) {
        ItemStack upgrade = new ItemStack(rangeUpgradeMaterial);
        ItemMeta meta = upgrade.getItemMeta();
        try {
            meta.displayName(lang.getItemName("upgrade.range." + (tier-1)));
            meta.lore(lang.getItemLore("upgrade.range"));
        } catch (de.kwantux.config.util.exceptions.InvalidNodeException e) {
            logger.warning("Missing language key for range upgrade tier " + tier);
        }
        setCustomModelDataForRangeUpgrade(meta, tier);
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "upgrade.range"), PersistentDataType.INTEGER, tier);
        upgrade.setItemMeta(meta);
        return upgrade;
    }


    public CraftingManager(Main main) {

        this.plugin = main;

        this.config = new SimpleConfig(main, "recipes.conf");

        // Define all default recipe configurations with comments
        defineDefaults();

        this.lang = main.getLanguage();
        this.logger = main.getLogger();
        this.pluginconfig = main.getConfiguration();


        registerRecipes();

        main.getLogger().info("Initialiased Crafting Recipes");
    }

    /**
     * Define all default recipe configurations with comments
     */
    private void defineDefaults() {
        // Network wand recipe (3x3 grid)
        config.defineDefault("wand", new String[]{"EMPTY", "REDSTONE", "GLOWSTONE_DUST", "REDSTONE", "BLAZE_ROD", "REDSTONE", "GLOWSTONE_DUST", "REDSTONE", "EMPTY"}, "Network wand crafting recipe (3x3 grid). Use EMPTY for empty slots");

        // Component recipes - input container
        config.defineDefault("component.input", new String[]{"EMPTY", "BASE_ITEM", "EMPTY", "REDSTONE", "HOPPER", "REDSTONE", "EMPTY", "REDSTONE", "EMPTY"}, "Recipe for input component. BASE_ITEM will be replaced with the configured component upgrade material");

        // Component recipes - misc container
        config.defineDefault("component.misc", new String[]{"EMPTY", "REDSTONE", "EMPTY", "REDSTONE", "HOPPER", "REDSTONE", "EMPTY", "BASE_ITEM", "EMPTY"}, "Recipe for misc component. BASE_ITEM will be replaced with the configured component upgrade material");

        // Component recipes - sorting container
        config.defineDefault("component.sorting", new String[]{"HOPPER", "COMPARATOR", "REDSTONE", "HOPPER", "EMPTY", "REDSTONE", "BASE_ITEM", "REDSTONE_TORCH", "REPEATER"}, "Recipe for sorting component. BASE_ITEM will be replaced with the configured component upgrade material");

        // Range upgrade recipes (tier 0-4)
        config.defineDefault("upgrade.range.0", new String[]{"EMPTY", "REDSTONE", "EMPTY", "REDSTONE", "LIGHTNING_ROD", "REDSTONE", "EMPTY", "REDSTONE", "EMPTY"}, "Range upgrade tier 1 recipe");
        config.defineDefault("upgrade.range.1", new String[]{"REDSTONE_BLOCK", "LIGHTNING_ROD", "REDSTONE_BLOCK", "GOLD_INGOT", "LIGHTNING_ROD", "GOLD_INGOT", "REDSTONE_BLOCK", "LIGHTNING_ROD", "REDSTONE_BLOCK"}, "Range upgrade tier 2 recipe");
        config.defineDefault("upgrade.range.2", new String[]{"ENDER_PEARL", "COPPER_BLOCK", "ENDER_PEARL", "GOLD_BLOCK", "COPPER_BLOCK", "GOLD_BLOCK", "REDSTONE_BLOCK", "COPPER_BLOCK", "REDSTONE_BLOCK"}, "Range upgrade tier 3 recipe");
        config.defineDefault("upgrade.range.3", new String[]{"ENDER_PEARL", "GOLD_BLOCK", "ENDER_PEARL", "SCULK", "COPPER_BLOCK", "SCULK", "SCULK", "COPPER_BLOCK", "SCULK"}, "Range upgrade tier 4 recipe");
        config.defineDefault("upgrade.range.4", new String[]{"ENDER_EYE", "ENDER_EYE", "ENDER_EYE", "AMETHYST_SHARD", "ECHO_SHARD", "AMETHYST_SHARD", "NETHERITE_SCRAP", "DIAMOND", "NETHERITE_SCRAP"}, "Range upgrade tier 5 recipe");
    }

    private void registerRecipes() {
        registerItem("wand", getNetworkWand(2));
        registerComponent(ComponentType.INPUT);
        registerComponent(ComponentType.MISC);
        registerComponent(ComponentType.SORTING);
        registerRangeUpgrades();
    }

    private void registerItem(String path, ItemStack result) {
        NamespacedKey key = new NamespacedKey(plugin, path.replace(".", "_"));
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        String[] ingredients = config.getStringArray(path);
        if (ingredients == null) {
            logger.warning("Skipping recipe " + path + " due to missing configuration");
            return;
        }
        
        String[] shape = new String[9];

        for (int i = 0; i < 9; i++) {
            if (ingredients[i].equalsIgnoreCase("AIR") || ingredients[i].equalsIgnoreCase("EMPTY")) {
                shape[i] = " ";
            } else {
                shape[i] = String.valueOf(keys[i]);
            }

        }

        recipe.shape(shape[0] + shape[1] + shape[2], shape[3] + shape[4] + shape[5], shape[6] + shape[7] + shape[8]);

        for (int i = 0; i < 9; i++) {
            if (!shape[i].equalsIgnoreCase(" ")) {
                try {
                    recipe.setIngredient(keys[i], Material.valueOf(ingredients[i]));
                }
                catch (IllegalArgumentException e) {
                    logger.severe(ingredients[i] + " is not a valid material, it will replaced with AIR. Recipe " + path + " may be broken.");
                    recipe.setIngredient(keys[i], Material.AIR);
                }
            }
        }

        Bukkit.addRecipe(recipe);
        recipes.add(key);
    }


    private void registerComponent(ComponentType type) {

        String path = "component."+ type.tag;
        String matkey = componentUpgradeMaterial.name();
        ItemStack stack = type.item();

        NamespacedKey key = new NamespacedKey(plugin, path.replace(".", "_"));
        ShapedRecipe recipe = new ShapedRecipe(key, stack);

        String[] ingredients = config.getStringArray(path);
        if (ingredients == null) {
            logger.warning("Skipping recipe " + path + " due to missing configuration");
            return;
        }

        for (int i = 0; i < ingredients.length; i++) {
            if (ingredients[i].equalsIgnoreCase("BASE_ITEM")) ingredients[i] = matkey;
        }

        String[] shape = new String[9];

        for (int i = 0; i < 9; i++) {
            if (ingredients[i].equalsIgnoreCase("AIR") || ingredients[i].equalsIgnoreCase("EMPTY")) {
                shape[i] = " ";
            } else {
                shape[i] = String.valueOf(keys[i]);
            }

        }

        recipe.shape(shape[0] + shape[1] + shape[2], shape[3] + shape[4] + shape[5], shape[6] + shape[7] + shape[8]);

        for (int i = 0; i < 9; i++) {
            if (!shape[i].equalsIgnoreCase(" ")) {
                try {
                    recipe.setIngredient(keys[i], Material.valueOf(ingredients[i]));
                }
                catch (IllegalArgumentException e) {
                    logger.severe(ingredients[i] + " is not a valid material, it will replaced with AIR. Recipe " + path + " may be broken.");
                    recipe.setIngredient(keys[i], Material.AIR);
                }
            }
        }

        Bukkit.addRecipe(recipe);
        recipes.add(key);
    }

    private void registerRangeUpgrades() {
        for (int i = 0; i < 5; i++) {
            String path = "upgrade.range." + i;
            String[] ingredients = config.getStringArray(path);
            if (ingredients == null) {
                logger.warning("Skipping range upgrade recipe " + path + " due to missing configuration");
                continue;
            }

            NamespacedKey key = new NamespacedKey(plugin, path.replace(".", "_"));
            ShapedRecipe recipe = new ShapedRecipe(key, getRangeUpgrade(i + 1));

            String[] shape = new String[9];

            for (int j = 0; j < 9; j++) {
                if (ingredients[j].equalsIgnoreCase("EMPTY") || ingredients[j].equalsIgnoreCase("AIR")) {
                    shape[j] = " ";
                } else {
                    shape[j] = String.valueOf(keys[j]);
                }
            }

            recipe.shape(shape[0] + shape[1] + shape[2], shape[3] + shape[4] + shape[5], shape[6] + shape[7] + shape[8]);

            for (int j = 0; j < 9; j++) {
                if (!shape[j].equalsIgnoreCase(" ")) {
                    try {
                        recipe.setIngredient(keys[j], Material.valueOf(ingredients[j]));
                    } catch (IllegalArgumentException e) {
                        logger.severe(ingredients[j] + " is not a valid material, it will replaced with AIR. Recipe " + path + " may be broken.");
                        recipe.setIngredient(keys[j], Material.AIR);
                    }
                }
            }

            Bukkit.addRecipe(recipe);
            recipes.add(key);
        }
    }

    public static void setCustomModelDataForWand(ItemMeta meta, int mode) {
        CustomModelDataComponent cmdc = meta.getCustomModelDataComponent();
        cmdc.setStrings(List.of("networks:wand", "networks:wand/"+mode));
        meta.setCustomModelDataComponent(cmdc);
    }

    public static void setCustomModelDataForRangeUpgrade(ItemMeta meta, int tier) {
        CustomModelDataComponent cmdc = meta.getCustomModelDataComponent();
        cmdc.setStrings(List.of("networks:upgrade/range", "networks:upgrade/range/"+tier));
        meta.setCustomModelDataComponent(cmdc);
    }

    public static void setCustomModelDataForComponent(ItemMeta meta, ComponentType type) {
        CustomModelDataComponent cmdc = meta.getCustomModelDataComponent();
        cmdc.setStrings(List.of("networks:component/"+type.tag));
        meta.setCustomModelDataComponent(cmdc);
    }
}
