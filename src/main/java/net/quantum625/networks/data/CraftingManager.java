package net.quantum625.networks.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.quantum625.config.Configuration;
import net.quantum625.config.lang.LanguageController;
import net.quantum625.config.util.exceptions.ConfigAlreadyRegisteredException;
import net.quantum625.config.util.exceptions.InvalidNodeException;
import net.quantum625.networks.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CraftingManager {

    private final Main plugin;
    private final Configuration config;
    private final Logger logger;
    private final LanguageController lang;

    private final Config pluginconfig;


    private Material rangeUpgradeMaterial;
    private Material componentUpgradeMaterial;



    public void save() {
        config.save();
    }


    private static final char[] keys = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};


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

    public ItemStack getInputContainer() {

        ItemStack inputContainer = new ItemStack(componentUpgradeMaterial);
        ItemMeta meta = inputContainer.getItemMeta();
        try {
            meta.displayName(lang.getItemName("input"));
            meta.lore(lang.getItemLore("input"));
        } catch (InvalidNodeException e) {
            throw new RuntimeException(e);
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "component"), PersistentDataType.STRING, "input");
        inputContainer.setItemMeta(meta);
        return inputContainer;
    }

    public ItemStack getInputContainer(Material material) {

        ItemStack inputContainer = new ItemStack(material);
        ItemMeta meta = inputContainer.getItemMeta();
        try {
            meta.displayName(lang.getItemName("input"));
            meta.lore(lang.getItemLore("input"));
        } catch (InvalidNodeException e) {
            throw new RuntimeException(e);
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "component_type"), PersistentDataType.STRING, "input");
        inputContainer.setItemMeta(meta);
        return inputContainer;
    }

    public ItemStack getSortingContainer() {
        ItemStack sortingContainer = new ItemStack(componentUpgradeMaterial);
        ItemMeta meta = sortingContainer.getItemMeta();
        try {
            meta.displayName(lang.getItemName("sorting"));
            meta.lore(lang.getItemLore("sorting"));
        } catch (InvalidNodeException e) {
            throw new RuntimeException(e);
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "component"), PersistentDataType.STRING, "sorting");
        sortingContainer.setItemMeta(meta);
        return sortingContainer;
    }
    public ItemStack getSortingContainer(Material material) {
        ItemStack sortingContainer = new ItemStack(material);
        ItemMeta meta = sortingContainer.getItemMeta();
        try {
            meta.displayName(lang.getItemName("sorting"));
            meta.lore(lang.getItemLore("sorting"));
        } catch (InvalidNodeException e) {
            throw new RuntimeException(e);
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "component_type"), PersistentDataType.STRING, "sorting");
        sortingContainer.setItemMeta(meta);
        return sortingContainer;
    }

    public ItemStack getSortingContainer(Material material, String[] items) {
        ItemStack sortingContainer = getSortingContainer(material);
        ItemMeta meta = sortingContainer.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        String itemString = "";
        List<Component> lore = meta.lore();
        for (String itemType : items) {
            itemString += "," + itemType;
            lore.add(Component.text(itemType.toUpperCase()).decoration(TextDecoration.ITALIC, false));
        }
        if (itemString.length() > 1) {
            itemString = itemString.substring(1);
        }
        meta.lore(lore);
        dataContainer.set(new NamespacedKey(plugin, "filter_items"), PersistentDataType.STRING, itemString);
        sortingContainer.setItemMeta(meta);
        return sortingContainer;
    }



    public ItemStack getMiscContainer() {
        ItemStack miscContainer = new ItemStack(componentUpgradeMaterial);
        ItemMeta meta = miscContainer.getItemMeta();
        try {
            meta.displayName(lang.getItemName("misc"));
            meta.lore(lang.getItemLore("misc"));
        } catch (InvalidNodeException e) {
            throw new RuntimeException(e);
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "component"), PersistentDataType.STRING, "misc");
        miscContainer.setItemMeta(meta);
        return miscContainer;
    }

    public ItemStack getMiscContainer(Material material) {
        ItemStack miscContainer = new ItemStack(material);
        ItemMeta meta = miscContainer.getItemMeta();
        try {
            meta.displayName(lang.getItemName("misc"));
            meta.lore(lang.getItemLore("misc"));
        } catch (InvalidNodeException e) {
            throw new RuntimeException(e);
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "component_type"), PersistentDataType.STRING, "misc");
        miscContainer.setItemMeta(meta);
        return miscContainer;
    }

    public ItemStack getRangeUpgrade(int tier) throws InvalidNodeException {
        ItemStack upgrade = new ItemStack(Material.LIGHTNING_ROD);
        ItemMeta meta = upgrade.getItemMeta();
        meta.displayName(lang.getItemName("upgrade.range." + tier));
        meta.lore(lang.getItemLore("upgrade.range"));
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "upgrade.range"), PersistentDataType.INTEGER, tier+1);
        upgrade.setItemMeta(meta);
        return upgrade;
    }


    public CraftingManager(Main main) {

        this.plugin = main;

        try {
            this.config = Configuration.create(main, "recipes", "recipes.conf");
        } catch (ConfigAlreadyRegisteredException e) {
            throw new RuntimeException(e);
        }

        this.lang = main.getLanguage();
        this.logger = main.getLogger();
        this.pluginconfig = main.getConfiguration();

        componentUpgradeMaterial = pluginconfig.getComponentUpgradeMaterial();
        rangeUpgradeMaterial = pluginconfig.getRangeUpgradeMaterial();

        registerRecipes();
        /*try {
        } catch (InvalidNodeException e) {
            logger.severe("A recipe couln't be loaded, because the config file recipes.yml is missing data. Please delete that file and restart the server!");
            throw new RuntimeException(e);
        }*/

        main.getLogger().info("Initialiased Crafting Recipes");
    }

    private void registerRecipes() {
        registerItem("wand", getNetworkWand(0));
        registerItem("component.input.upgrade", getInputContainer());
        registerItem("component.sorting.upgrade", getSortingContainer());
        registerItem("component.misc.upgrade", getMiscContainer());
        registerRangeUpgrades();
    }

    private void registerItem(String path, ItemStack result) {
        try {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, path.replace(".", "_")), result);

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
        }
        catch (InvalidNodeException | SerializationException e) {
            logger.severe("Config file recipes.conf seems to have an invalid format or is missing some data, please delete this file, restart the server and try again!");
            throw new RuntimeException(e);
        }

    }

    private void registerComponent(String type, Map<Material, ItemStack> component, ItemStack installable) {

        registerItem("component." + type + ".upgrade", installable);

        String path = "component." + type + ".block";

        try {

            for (String container_key : pluginconfig.getContainerWhitelist()) {

                Material baseMaterial = Material.valueOf(container_key);

                ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, path.replace(".", "_") + container_key.toLowerCase()), component.get(baseMaterial));

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

                        Material material;
                        try {
                            material = Material.valueOf(ingredients.get(i));
                        }
                        catch (EnumConstantNotPresentException e) {
                            if (ingredients.get(i).equalsIgnoreCase("BASE_ITEM")) {
                                material = baseMaterial;
                            }
                            else  {
                                logger.severe(ingredients.get(i) + " is not a valid material, it will replaced with AIR. Recipe " + path + " may be broken.");
                                material = Material.AIR;
                            }
                        }
                        recipe.setIngredient(keys[i], material);
                    }
                }

                Bukkit.addRecipe(recipe);
            }
        }
        catch (InvalidNodeException | SerializationException e) {
            logger.severe("Config file recipes.conf seems to have an invalid format or is missing some data, please delete this file, restart the server and try again!");
            throw new RuntimeException(e);
        }
    }

    private void registerRangeUpgrades() {
        try {
            for (int i = 0; i < config.get("upgrade.range").childrenList().size(); i++) {

                String path = "upgrade.range." + i;

                ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, path.replace(".", "_")), getRangeUpgrade(i));

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
            }
        }
        catch (InvalidNodeException | SerializationException e) {
            logger.severe("Config file recipes.conf seems to have an invalid format or is missing some data, please delete this file, restart the server and try again!");
            throw new RuntimeException(e);
        }
    }
}
