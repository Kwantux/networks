package com.quantum625.networks.data;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

    private ItemStack inputContainer = new ItemStack(Material.CHEST);
    private ItemStack sortingContainer = new ItemStack(Material.CHEST, 1);
    private ItemStack miscContainer = new ItemStack(Material.CHEST, 1);

    public ShapedRecipe inputContainerRecipe;
    public ShapedRecipe sortingContainerRecipe = new ShapedRecipe(new NamespacedKey("networks","sorting_container"), sortingContainer);
    public ShapedRecipe miscContainerRecipe = new ShapedRecipe(new NamespacedKey("networks","misc_container"), miscContainer);


    public CraftingManager(File dataFolder) {

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

        ItemMeta meta = inputContainer.getItemMeta();
        meta.setDisplayName("§rInput Container");
        meta.setLore(Arrays.asList("§r§9Sorts items into sorting chests and misc chests"));
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "input");
        inputContainer.setItemMeta(meta);

        inputContainerRecipe = new ShapedRecipe(new NamespacedKey("networks","input_container"), inputContainer);

        inputContainerRecipe.shape("ABC","DEF","GHI");
        inputContainerRecipe.setIngredient('A', Material.valueOf(config.get("input.ingredient1").toString()));
        inputContainerRecipe.setIngredient('B', Material.valueOf(config.get("input.ingredient2").toString()));
        inputContainerRecipe.setIngredient('C', Material.valueOf(config.get("input.ingredient3").toString()));
        inputContainerRecipe.setIngredient('D', Material.valueOf(config.get("input.ingredient4").toString()));
        inputContainerRecipe.setIngredient('E', Material.valueOf(config.get("input.ingredient5").toString()));
        inputContainerRecipe.setIngredient('F', Material.valueOf(config.get("input.ingredient6").toString()));
        inputContainerRecipe.setIngredient('G', Material.valueOf(config.get("input.ingredient7").toString()));
        inputContainerRecipe.setIngredient('H', Material.valueOf(config.get("input.ingredient8").toString()));
        inputContainerRecipe.setIngredient('I', Material.valueOf(config.get("input.ingredient9").toString()));




        meta = sortingContainer.getItemMeta();
        meta.setDisplayName("§rSorting Container");
        meta.setLore(Arrays.asList("§rFiltered Items:"));
        data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "sorting");
        data.set(new NamespacedKey("networks", "filter_items"), PersistentDataType.STRING, "");
        sortingContainer.setItemMeta(meta);

        sortingContainerRecipe = new ShapedRecipe(new NamespacedKey("networks","sorting_container"), sortingContainer);

        sortingContainerRecipe.shape("ABC","DEF","GHI");
        sortingContainerRecipe.setIngredient('A', Material.valueOf(config.get("sorting.ingredient1").toString()));
        sortingContainerRecipe.setIngredient('B', Material.valueOf(config.get("sorting.ingredient2").toString()));
        sortingContainerRecipe.setIngredient('C', Material.valueOf(config.get("sorting.ingredient3").toString()));
        sortingContainerRecipe.setIngredient('D', Material.valueOf(config.get("sorting.ingredient4").toString()));
        sortingContainerRecipe.setIngredient('E', Material.valueOf(config.get("sorting.ingredient5").toString()));
        sortingContainerRecipe.setIngredient('F', Material.valueOf(config.get("sorting.ingredient6").toString()));
        sortingContainerRecipe.setIngredient('G', Material.valueOf(config.get("sorting.ingredient7").toString()));
        sortingContainerRecipe.setIngredient('H', Material.valueOf(config.get("sorting.ingredient8").toString()));
        sortingContainerRecipe.setIngredient('I', Material.valueOf(config.get("sorting.ingredient9").toString()));




        meta = miscContainer.getItemMeta();
        meta.setDisplayName("§rMiscellaneous Container");
        meta.setLore(Arrays.asList("§r§9All remaining items will go into these chests"));
        data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "misc");
        miscContainer.setItemMeta(meta);

        miscContainerRecipe = new ShapedRecipe(new NamespacedKey("networks","misc_container"), miscContainer);

        miscContainerRecipe.shape("ABC","DEF","GHI");
        miscContainerRecipe.setIngredient('A', Material.valueOf(config.get("misc.ingredient1").toString()));
        miscContainerRecipe.setIngredient('B', Material.valueOf(config.get("misc.ingredient2").toString()));
        miscContainerRecipe.setIngredient('C', Material.valueOf(config.get("misc.ingredient3").toString()));
        miscContainerRecipe.setIngredient('D', Material.valueOf(config.get("misc.ingredient4").toString()));
        miscContainerRecipe.setIngredient('E', Material.valueOf(config.get("misc.ingredient5").toString()));
        miscContainerRecipe.setIngredient('F', Material.valueOf(config.get("misc.ingredient6").toString()));
        miscContainerRecipe.setIngredient('G', Material.valueOf(config.get("misc.ingredient7").toString()));
        miscContainerRecipe.setIngredient('H', Material.valueOf(config.get("misc.ingredient8").toString()));
        miscContainerRecipe.setIngredient('I', Material.valueOf(config.get("misc.ingredient9").toString()));
    }
}
