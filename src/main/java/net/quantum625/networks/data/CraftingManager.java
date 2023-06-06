package net.quantum625.networks.data;

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

public class CraftingManager {

    private Configuration config;
    private LanguageController lang;



    public void save() {
        config.save();
    }


    private char[] keys = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};

    public ShapedRecipe wandRecipe;
    public ShapedRecipe inputContainerRecipe;
    public ShapedRecipe sortingContainerRecipe;
    public ShapedRecipe miscContainerRecipe;

    public ShapedRecipe upgradeRecipe;


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
        data.set(new NamespacedKey("networks", "wand"), PersistentDataType.INTEGER, mode);
        wand.setItemMeta(meta);
        return wand;
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
        data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "input");
        inputContainer.setItemMeta(meta);
        return inputContainer;
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
        data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "sorting");
        data.set(new NamespacedKey("networks", "filter_items"), PersistentDataType.STRING, ",");
        sortingContainer.setItemMeta(meta);
        return sortingContainer;
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
        data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "misc");
        miscContainer.setItemMeta(meta);
        return miscContainer;
    }

    public ItemStack getRangeUpgrade(int tier) throws InvalidNodeException {
        ItemStack upgrade = new ItemStack(Material.LIGHTNING_ROD);
        ItemMeta meta = upgrade.getItemMeta();
        meta.displayName(lang.getItemName("upgrade" + tier));
        meta.lore(lang.getItemLore("upgrade"));
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey("networks", "upgrade"), PersistentDataType.INTEGER, tier);
        upgrade.setItemMeta(meta);
        return upgrade;
    }


    public CraftingManager(Main main) {

        try {
            this.config = Configuration.create(main, "recipes", "recipes.conf");
        } catch (ConfigAlreadyRegisteredException e) {
            throw new RuntimeException(e);
        }

        this.lang = main.getLanguage();
        Config pluginconfig = main.getConfiguration();


        try {
            wandRecipe = new ShapedRecipe(new NamespacedKey("networks", "wand"), getNetworkWand(0));

            String[] ingredients = new String[9];
            String[] shape = new String[9];

            for (int i = 0; i < 9; i++) {
                ingredients[i] = config.getString("wand.ingredient" + (i + 1));
                if (ingredients[i].equalsIgnoreCase("AIR") || ingredients[i].equalsIgnoreCase("EMPTY")) {
                    shape[i] = " ";
                } else {
                    shape[i] = "" + keys[i];
                }

            }

            wandRecipe.shape(shape[0] + shape[1] + shape[2], shape[3] + shape[4] + shape[5], shape[6] + shape[7] + shape[8]);

            for (int i = 0; i < 9; i++) {
                if (!shape[i].equalsIgnoreCase(" ")) {
                    wandRecipe.setIngredient(keys[i], Material.valueOf(config.getString("wand.ingredient" + (i + 1))));
                }
            }

            Bukkit.addRecipe(wandRecipe);


            for (String container_key : pluginconfig.getContainerWhitelist()) {

                inputContainerRecipe = new ShapedRecipe(new NamespacedKey("networks", "input_container_" + container_key.toLowerCase()), getInputContainer(Material.valueOf(container_key)));

                for (int i = 0; i < 9; i++) {
                    ingredients[i] = config.getString("input.ingredient" + (i + 1));
                    if (ingredients[i].equalsIgnoreCase("AIR") || ingredients[i].equalsIgnoreCase("EMPTY")) {
                        shape[i] = " ";
                    } else {
                        shape[i] = "" + keys[i];
                    }

                }

                inputContainerRecipe.shape(shape[0] + shape[1] + shape[2], shape[3] + shape[4] + shape[5], shape[6] + shape[7] + shape[8]);

                for (int i = 0; i < 9; i++) {
                    if (!shape[i].equalsIgnoreCase(" ")) {
                        if (config.getString("input.ingredient" + (i + 1)).equalsIgnoreCase("BASE_ITEM")) {
                            inputContainerRecipe.setIngredient(keys[i], Material.valueOf(container_key));
                        } else {
                            inputContainerRecipe.setIngredient(keys[i], Material.valueOf(config.getString("input.ingredient" + (i + 1))));
                        }
                    }
                }

                Bukkit.addRecipe(inputContainerRecipe);


                sortingContainerRecipe = new ShapedRecipe(new NamespacedKey("networks", "sorting_container_" + container_key.toLowerCase()), getSortingContainer(Material.valueOf(container_key)));

                for (int i = 0; i < 9; i++) {
                    ingredients[i] = config.getString("sorting.ingredient" + (i + 1));
                    if (ingredients[i].equalsIgnoreCase("AIR") || ingredients[i].equalsIgnoreCase("EMPTY")) {
                        shape[i] = " ";
                    } else {
                        shape[i] = "" + keys[i];
                    }

                }

                sortingContainerRecipe.shape(shape[0] + shape[1] + shape[2], shape[3] + shape[4] + shape[5], shape[6] + shape[7] + shape[8]);

                for (int i = 0; i < 9; i++) {
                    if (!shape[i].equalsIgnoreCase(" ")) {
                        if (config.getString("sorting.ingredient" + (i + 1)).equalsIgnoreCase("BASE_ITEM")) {
                            sortingContainerRecipe.setIngredient(keys[i], Material.valueOf(container_key));
                        } else {
                            sortingContainerRecipe.setIngredient(keys[i], Material.valueOf(config.getString("sorting.ingredient" + (i + 1))));
                        }
                    }
                }

                Bukkit.addRecipe(sortingContainerRecipe);


                miscContainerRecipe = new ShapedRecipe(new NamespacedKey("networks", "misc_container_" + container_key.toLowerCase()), getMiscContainer(Material.valueOf(container_key)));

                for (int i = 0; i < 9; i++) {
                    ingredients[i] = config.getString("misc.ingredient" + (i + 1));
                    if (ingredients[i].equalsIgnoreCase("AIR") || ingredients[i].equalsIgnoreCase("EMPTY")) {
                        shape[i] = " ";
                    } else {
                        shape[i] = "" + keys[i];
                    }

                }

                miscContainerRecipe.shape(shape[0] + shape[1] + shape[2], shape[3] + shape[4] + shape[5], shape[6] + shape[7] + shape[8]);

                for (int i = 0; i < 9; i++) {
                    if (!shape[i].equalsIgnoreCase(" ")) {
                        if (config.getString("misc.ingredient" + (i + 1)).equalsIgnoreCase("BASE_ITEM")) {
                            miscContainerRecipe.setIngredient(keys[i], Material.valueOf(container_key));
                        } else {
                            miscContainerRecipe.setIngredient(keys[i], Material.valueOf(config.getString("misc.ingredient" + (i + 1))));
                        }
                    }
                }

                Bukkit.addRecipe(miscContainerRecipe);

            }

            boolean running = true;

            for (int j = 1; running; j++) {

                try {
                    upgradeRecipe = new ShapedRecipe(new NamespacedKey("networks", "upgrade" + j), getRangeUpgrade(j));
                }
                catch (InvalidNodeException e) {
                    running = false;
                }

                ingredients = new String[9];
                shape = new String[9];

                for (int i = 0; i < 9; i++) {
                    if (!config.has("upgrade" + j + ".ingredient" + (i + 1))) running = false;
                    else {
                        ingredients[i] = config.getString("upgrade" + j + ".ingredient" + (i + 1));
                        if (ingredients[i].equalsIgnoreCase("AIR") || ingredients[i].equalsIgnoreCase("EMPTY")) {
                            shape[i] = " ";
                        } else {
                            shape[i] = "" + keys[i];
                        }
                    }

                }

                if (running) {
                    upgradeRecipe.shape(shape[0] + shape[1] + shape[2], shape[3] + shape[4] + shape[5], shape[6] + shape[7] + shape[8]);

                    for (int i = 0; i < 9; i++) {
                        if (!shape[i].equalsIgnoreCase(" ")) {
                            upgradeRecipe.setIngredient(keys[i], Material.valueOf(config.getString("upgrade" + j + ".ingredient" + (i + 1))));
                        }
                    }

                    Bukkit.addRecipe(upgradeRecipe);
                }

            }

            main.getLogger().info("Initialiased Crafting Recipes");
        }
        catch (InvalidNodeException e) {
            main.getLogger().severe("Config error found during initialisation of crafting recipes, a configuration is missing a value");
            throw new RuntimeException(e);
        }
    }
}
