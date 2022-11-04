package com.quantum625.networks.data;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CraftingManager {

    private File file;
    private FileConfiguration config;

    public void save() {
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Config pluginconfig;

    private char[] keys = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};
    private ItemStack wand = new ItemStack(Material.BLAZE_ROD);
    private ItemStack inputContainer;
    private ItemStack sortingContainer;
    private ItemStack miscContainer;

    public ShapedRecipe wandRecipe;
    public ShapedRecipe inputContainerRecipe;
    public ShapedRecipe sortingContainerRecipe;
    public ShapedRecipe miscContainerRecipe;


    public CraftingManager(File dataFolder, Config pluginconfig) {

        this.pluginconfig = pluginconfig;

        this.file = new File(dataFolder, "recipes.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);



        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName("§r§cNetwork Wand");
        meta.setLore(Arrays.asList("§rRight click any network component to show information about it", "§rSneak + Right click on a sorting chest with an item in the offhand to apply a filter", "§rSneak + Left click with an item in the offhand to remove a filter"));
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey("networks", "wand"), PersistentDataType.INTEGER, 1);
        wand.setItemMeta(meta);

        wandRecipe = new ShapedRecipe(new NamespacedKey("networks","wand"), wand);

        String[] ingredients = new String[9];
        String[] shape = new String[9];

        for (int i = 0; i < 9; i++) {
            if (!config.contains("wand.ingredient"+(i+1))) {
                Bukkit.getLogger().warning("[Networks] No item set for wand.ingredient"+(i+1)+ " found in recipes.yml!");
                Bukkit.getLogger().warning("[Networks] If you want the slot to be empty, insert AIR as item type.");
            }
            ingredients[i] = config.get("wand.ingredient"+(i+1)).toString();
            if (ingredients[i].equalsIgnoreCase("AIR") || ingredients[i].equalsIgnoreCase("EMPTY")) {
                shape[i] = " ";
            }
            else {
                shape[i] = ""+keys[i];
            }

        }

        wandRecipe.shape(shape[0]+shape[1]+shape[2],shape[3]+shape[4]+shape[5],shape[6]+shape[7]+shape[8]);

        for (int i = 0; i < 9 ; i++) {
            if (!shape[i].equalsIgnoreCase(" ")) {
                wandRecipe.setIngredient(keys[i], Material.valueOf(config.get("wand.ingredient" + (i+1)).toString()));
            }
        }

        Bukkit.addRecipe(wandRecipe);


        
        for (String container_key : pluginconfig.getContainerWhitelist()) {

            inputContainer = new ItemStack(Material.valueOf(container_key));

            meta = inputContainer.getItemMeta();
            meta.setDisplayName("§rInput Container");
            meta.setLore(Arrays.asList("§r§9Sorts items into sorting chests and misc chests"));
            data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "input");
            inputContainer.setItemMeta(meta);

            inputContainerRecipe = new ShapedRecipe(new NamespacedKey("networks", "input_container_"+container_key.toLowerCase()), inputContainer);

            for (int i = 0; i < 9; i++) {
                if (!config.contains("input.ingredient" + (i + 1))) {
                    Bukkit.getLogger().warning("[Networks] No item set for input.ingredient" + (i + 1) + " found in recipes.yml!");
                    Bukkit.getLogger().warning("[Networks] If you want the slot to be empty, insert AIR as item type.");
                }
                ingredients[i] = config.get("input.ingredient" + (i + 1)).toString();
                if (ingredients[i].equalsIgnoreCase("AIR") || ingredients[i].equalsIgnoreCase("EMPTY")) {
                    shape[i] = " ";
                } else {
                    shape[i] = "" + keys[i];
                }

            }

            inputContainerRecipe.shape(shape[0] + shape[1] + shape[2], shape[3] + shape[4] + shape[5], shape[6] + shape[7] + shape[8]);

            for (int i = 0; i < 9; i++) {
                if (!shape[i].equalsIgnoreCase(" ")) {
                    if (config.get("input.ingredient"+ (i + 1)).toString().equalsIgnoreCase("BASE_ITEM")) {
                        inputContainerRecipe.setIngredient(keys[i], Material.valueOf(container_key));
                    }
                    else {
                        inputContainerRecipe.setIngredient(keys[i], Material.valueOf(config.get("input.ingredient" + (i + 1)).toString()));
                    }
                }
            }

            Bukkit.addRecipe(inputContainerRecipe);



            sortingContainer = new ItemStack(Material.valueOf(container_key));

            meta = sortingContainer.getItemMeta();
            meta.setDisplayName("§rSorting Container");
            meta.setLore(Arrays.asList("§rFiltered Items:"));
            data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "sorting");
            data.set(new NamespacedKey("networks", "filter_items"), PersistentDataType.STRING, ",");
            sortingContainer.setItemMeta(meta);

            sortingContainerRecipe = new ShapedRecipe(new NamespacedKey("networks", "sorting_container_"+container_key.toLowerCase()), sortingContainer);

            for (int i = 0; i < 9; i++) {
                if (!config.contains("sorting.ingredient" + (i + 1))) {
                    Bukkit.getLogger().warning("[Networks] No item set for sorting.ingredient" + (i + 1) + " found in recipes.yml!");
                    Bukkit.getLogger().warning("[Networks] If you want the slot to be empty, insert AIR as item type.");
                }
                ingredients[i] = config.get("sorting.ingredient" + (i + 1)).toString();
                if (ingredients[i].equalsIgnoreCase("AIR") || ingredients[i].equalsIgnoreCase("EMPTY")) {
                    shape[i] = " ";
                } else {
                    shape[i] = "" + keys[i];
                }

            }

            sortingContainerRecipe.shape(shape[0] + shape[1] + shape[2], shape[3] + shape[4] + shape[5], shape[6] + shape[7] + shape[8]);

            for (int i = 0; i < 9; i++) {
                if (!shape[i].equalsIgnoreCase(" ")) {
                    if (config.get("sorting.ingredient"+ (i + 1)).toString().equalsIgnoreCase("BASE_ITEM")) {
                        sortingContainerRecipe.setIngredient(keys[i], Material.valueOf(container_key));
                    }
                    else {
                        sortingContainerRecipe.setIngredient(keys[i], Material.valueOf(config.get("sorting.ingredient" + (i + 1)).toString()));
                    }
                }
            }

            Bukkit.addRecipe(sortingContainerRecipe);


            miscContainer = new ItemStack(Material.valueOf(container_key));

            meta = miscContainer.getItemMeta();
            meta.setDisplayName("§rMiscellaneous Container");
            meta.setLore(Arrays.asList("§r§9All remaining items will go into these chests"));
            data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "misc");
            miscContainer.setItemMeta(meta);

            miscContainerRecipe = new ShapedRecipe(new NamespacedKey("networks", "misc_container_"+container_key.toLowerCase()), miscContainer);

            for (int i = 0; i < 9; i++) {
                if (!config.contains("misc.ingredient" + (i + 1))) {
                    Bukkit.getLogger().warning("[Networks] No item set for misc.ingredient" + (i + 1) + " found in recipes.yml!");
                    Bukkit.getLogger().warning("[Networks] If you want the slot to be empty, insert AIR as item type.");
                }
                ingredients[i] = config.get("misc.ingredient" + (i + 1)).toString();
                if (ingredients[i].equalsIgnoreCase("AIR") || ingredients[i].equalsIgnoreCase("EMPTY")) {
                    shape[i] = " ";
                } else {
                    shape[i] = "" + keys[i];
                }

            }

            miscContainerRecipe.shape(shape[0] + shape[1] + shape[2], shape[3] + shape[4] + shape[5], shape[6] + shape[7] + shape[8]);

            for (int i = 0; i < 9; i++) {
                if (!shape[i].equalsIgnoreCase(" ")) {
                    if (config.get("misc.ingredient"+ (i + 1)).toString().equalsIgnoreCase("BASE_ITEM")) {
                        miscContainerRecipe.setIngredient(keys[i], Material.valueOf(container_key));
                    }
                    else {
                        miscContainerRecipe.setIngredient(keys[i], Material.valueOf(config.get("misc.ingredient" + (i + 1)).toString()));
                    }
                }
            }

            Bukkit.addRecipe(miscContainerRecipe);

        }
    }
}
