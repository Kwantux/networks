package net.quantum625.networks.data;

import net.quantum625.networks.commands.LanguageModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;

public class CraftingManager {

    private File file;
    private FileConfiguration config;
    private LanguageModule lang;

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

    public ShapedRecipe wandRecipe;
    public ShapedRecipe inputContainerRecipe;
    public ShapedRecipe sortingContainerRecipe;
    public ShapedRecipe miscContainerRecipe;

    public ShapedRecipe upgradeRecipe;


    public ItemStack getNetworkWand(int mode) {
        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName(lang.getItemName("wand."+mode));
        meta.setLore(lang.getItemLore("wand."+mode));
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey("networks", "wand"), PersistentDataType.INTEGER, mode);
        wand.setItemMeta(meta);
        return wand;
    }

    public ItemStack getInputContainer(Material material) {
        ItemStack inputContainer = new ItemStack(material);
        ItemMeta meta = inputContainer.getItemMeta();
        meta.setDisplayName(lang.getItemName("input"));
        meta.setLore(lang.getItemLore("input"));
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "input");
        inputContainer.setItemMeta(meta);
        return inputContainer;
    }

    public ItemStack getSortingContainer(Material material) {
        ItemStack sortingContainer = new ItemStack(material);
        ItemMeta meta = sortingContainer.getItemMeta();
        meta.setDisplayName(lang.getItemName("sorting"));
        meta.setLore(lang.getItemLore("sorting"));
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "sorting");
        data.set(new NamespacedKey("networks", "filter_items"), PersistentDataType.STRING, ",");
        sortingContainer.setItemMeta(meta);
        return sortingContainer;
    }

    public ItemStack getMiscContainer(Material material) {
        ItemStack miscContainer = new ItemStack(material);
        ItemMeta meta = miscContainer.getItemMeta();
        meta.setDisplayName(lang.getItemName("misc"));
        meta.setLore(lang.getItemLore("misc"));
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "misc");
        miscContainer.setItemMeta(meta);
        return miscContainer;
    }

    public ItemStack getRangeUpgrade(int tier) {
        ItemStack upgrade = new ItemStack(Material.LIGHTNING_ROD);
        ItemMeta meta = upgrade.getItemMeta();
        meta.setDisplayName(lang.getItemName("upgrade",tier));
        meta.setLore(lang.getItemLore("upgrade"));
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey("networks", "upgrade"), PersistentDataType.INTEGER, tier);
        upgrade.setItemMeta(meta);
        return upgrade;
    }


    public CraftingManager(File dataFolder, Config pluginconfig, LanguageModule languageModule) {

        this.pluginconfig = pluginconfig;
        this.lang = languageModule;

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



        wandRecipe = new ShapedRecipe(new NamespacedKey("networks","wand"), getNetworkWand(0));

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

            inputContainerRecipe = new ShapedRecipe(new NamespacedKey("networks", "input_container_"+container_key.toLowerCase()), getInputContainer(Material.valueOf(container_key)));

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



            sortingContainerRecipe = new ShapedRecipe(new NamespacedKey("networks", "sorting_container_"+container_key.toLowerCase()), getSortingContainer(Material.valueOf(container_key)));

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


            miscContainerRecipe = new ShapedRecipe(new NamespacedKey("networks", "misc_container_"+container_key.toLowerCase()), getMiscContainer(Material.valueOf(container_key)));

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

        boolean running = true;

        for (int j = 1; running; j++) {

            upgradeRecipe = new ShapedRecipe(new NamespacedKey("networks", "upgrade"+j), getRangeUpgrade(j));

            ingredients = new String[9];
            shape = new String[9];

            for (int i = 0; i < 9; i++) {
                if (!config.contains("upgrade"+j+".ingredient" + (i + 1))) running = false;
                else {
                    ingredients[i] = config.get("upgrade" + j + ".ingredient" + (i + 1)).toString();
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
                        upgradeRecipe.setIngredient(keys[i], Material.valueOf(config.get("upgrade" + j + ".ingredient" + (i + 1)).toString()));
                    }
                }

                Bukkit.addRecipe(upgradeRecipe);
            }

        }
    }
}
