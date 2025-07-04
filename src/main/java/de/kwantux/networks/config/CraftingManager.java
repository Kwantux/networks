package de.kwantux.networks.config;

import de.kwantux.config.Configuration;
import de.kwantux.config.lang.LanguageController;
import de.kwantux.config.util.exceptions.ConfigAlreadyRegisteredException;
import de.kwantux.config.util.exceptions.InvalidNodeException;
import de.kwantux.networks.Main;
import de.kwantux.networks.compat.ConfigurationTransformers;
import de.kwantux.networks.component.util.ComponentType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CraftingManager {

    private final Main plugin;
    private final Configuration config;
    private final Logger logger;
    private final LanguageController lang;

    private final Config pluginconfig;


    private final Material rangeUpgradeMaterial;
    private final Material componentUpgradeMaterial;


    public static List<NamespacedKey> recipes = new ArrayList<>();



    public void save() {
        config.save();
    }


    private final char[] keys = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};


    public ItemStack getNetworkWand(int mode) {
        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wand.getItemMeta();
        try {
            meta.displayName(lang.getItemName("wand"+mode));
            meta.lore(lang.getItemLore("wand"+mode));
        } catch (InvalidNodeException e) {
            throw new RuntimeException(e);
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "wand"), PersistentDataType.INTEGER, mode);
        wand.setItemMeta(meta);
        return wand;
    }

    public ItemStack getRangeUpgrade(int tier) throws InvalidNodeException {
        ItemStack upgrade = new ItemStack(rangeUpgradeMaterial);
        ItemMeta meta = upgrade.getItemMeta();
        meta.displayName(lang.getItemName("upgrade.range." + (tier-1)));
        meta.lore(lang.getItemLore("upgrade.range"));
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "upgrade.range"), PersistentDataType.INTEGER, tier);
        upgrade.setItemMeta(meta);
        return upgrade;
    }


    public CraftingManager(Main main) {

        this.plugin = main;

        try {
            this.config = Configuration.create(main, "recipes", "recipes.conf");
            ConfigurationTransformers.recipesTransformers(config);
            config.update();

        } catch (ConfigAlreadyRegisteredException e) {
            throw new RuntimeException(e);
        }

        this.lang = main.getLanguage();
        this.logger = main.getLogger();
        this.pluginconfig = main.getConfiguration();

        componentUpgradeMaterial = pluginconfig.getComponentUpgradeMaterial();
        rangeUpgradeMaterial = pluginconfig.getRangeUpgradeMaterial();


        registerRecipes();

        main.getLogger().info("Initialiased Crafting Recipes");
    }

    private void registerRecipes() {
        registerItem("wand", getNetworkWand(2));
        for (ComponentType type : ComponentType.types.values()) {
            registerComponent(type);
        };
        registerRangeUpgrades();
    }

    private void registerItem(String path, ItemStack result) {
        try {
            NamespacedKey key = new NamespacedKey(plugin, path.replace(".", "_"));
            ShapedRecipe recipe = new ShapedRecipe(key, result);

            List<String> ingredients = config.getList(path, String.class);
            String[] shape = new String[9];

            for (int i = 0; i < 9; i++) {
                if (ingredients.get(i).equalsIgnoreCase("AIR") || ingredients.get(i).equalsIgnoreCase("EMPTY")) {
                    shape[i] = " ";
                } else {
                    shape[i] = String.valueOf(keys[i]);
                }

            }

            recipe.shape(shape[0] + shape[1] + shape[2], shape[3] + shape[4] + shape[5], shape[6] + shape[7] + shape[8]);

            for (int i = 0; i < 9; i++) {
                if (!shape[i].equalsIgnoreCase(" ")) {
                    try {
                        recipe.setIngredient(keys[i], Material.valueOf(ingredients.get(i)));
                    }
                    catch (IllegalArgumentException e) {
                        logger.severe(ingredients.get(i) + " is not a valid material, it will replaced with AIR. Recipe " + path + " may be broken.");
                        recipe.setIngredient(keys[i], Material.AIR);
                    }
                }
            }

            Bukkit.addRecipe(recipe);
            recipes.add(key);
        }
        catch (InvalidNodeException | SerializationException e) {
            logger.severe("Config file recipes.conf seems to have an invalid format or is missing some data, the cfg file was deleted, server will be restarted...");
            logger.severe("==============================================================================================================================================");
            File file = new File(plugin.getDataFolder(), "recipes.conf");
            file.delete();
            e.printStackTrace();
            Bukkit.shutdown();
        }

    }


    private void registerComponent(ComponentType type) {

        String path = "component."+ type.tag;
        String matkey = componentUpgradeMaterial.name();

        ItemStack stack = type.item();

        try {
            NamespacedKey key = new NamespacedKey(plugin, path.replace(".", "_"));
            ShapedRecipe recipe = new ShapedRecipe(key, stack);

            List<String> ingredients = config.getList(path, String.class);

            assert ingredients != null; //TODO: proper console warning

            for (String s : ingredients) {
                if (s.equalsIgnoreCase("BASE_ITEM")) s = matkey;
            }

            String[] shape = new String[9];

            for (int i = 0; i < 9; i++) {
                if (ingredients.get(i).equalsIgnoreCase("BASE_ITEM")) ingredients.set(i, matkey);
                if (ingredients.get(i).equalsIgnoreCase("AIR") || ingredients.get(i).equalsIgnoreCase("EMPTY")) {
                    shape[i] = " ";
                } else {
                    shape[i] = String.valueOf(keys[i]);
                }

            }

            recipe.shape(shape[0] + shape[1] + shape[2], shape[3] + shape[4] + shape[5], shape[6] + shape[7] + shape[8]);

            for (int i = 0; i < 9; i++) {
                if (!shape[i].equalsIgnoreCase(" ")) {
                    try {
                        recipe.setIngredient(keys[i], Material.valueOf(ingredients.get(i)));
                    }
                    catch (IllegalArgumentException e) {
                        logger.severe(ingredients.get(i) + " is not a valid material, it will replaced with AIR. Recipe " + path + " may be broken.");
                        recipe.setIngredient(keys[i], Material.AIR);
                    }
                }
            }

            Bukkit.addRecipe(recipe);
            recipes.add(key);

        }
        catch (InvalidNodeException | SerializationException e) {
            logger.severe("Config file recipes.conf seems to have an invalid format or is missing some data, the cfg file was deleted, server will be restarted...");
            logger.severe("==============================================================================================================================================");
            File file = new File(plugin.getDataFolder(), "recipes.conf");
            file.delete();
            e.printStackTrace();
            Bukkit.shutdown();
        }
    }

    private void registerRangeUpgrades() {
        try {
            for (int i = 1; i <= config.get("upgrade.range").childrenList().size(); i++) {

                String path = "upgrade.range." + (i-1);

                NamespacedKey key = new NamespacedKey(plugin, path.replace(".", "_"));

                ShapedRecipe recipe = new ShapedRecipe(key, getRangeUpgrade(i));

                List<String> ingredients = config.getList(path, String.class);
                String[] shape = new String[9];

                for (int j = 0; j < 9; j++) {
                    if (ingredients.get(j).equalsIgnoreCase("AIR") || ingredients.get(j).equalsIgnoreCase("EMPTY")) {
                        shape[j] = " ";
                    } else {
                        shape[j] = String.valueOf(keys[j]);
                    }

                }

                recipe.shape(shape[0] + shape[1] + shape[2], shape[3] + shape[4] + shape[5], shape[6] + shape[7] + shape[8]);

                for (int j = 0; j < 9; j++) {
                    if (!shape[j].equalsIgnoreCase(" ")) {
                        try {
                            recipe.setIngredient(keys[j], Material.valueOf(ingredients.get(j)));
                        } catch (IllegalArgumentException e) {
                            logger.severe(ingredients.get(j) + " is not a valid material, it will replaced with AIR. Recipe " + path + " may be broken.");
                            recipe.setIngredient(keys[j], Material.AIR);
                        }
                    }
                }

                Bukkit.addRecipe(recipe);
                recipes.add(key);
            }
        }
        catch (InvalidNodeException | SerializationException e) {
            logger.severe("Config file recipes.conf seems to have an invalid format or is missing some data, please delete this file, restart the server and try again!");
            throw new RuntimeException(e);
        }
    }
}
