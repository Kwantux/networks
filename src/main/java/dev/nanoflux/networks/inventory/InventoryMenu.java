package dev.nanoflux.networks.inventory;

import dev.nanoflux.networks.Network;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class InventoryMenu {

    Player player;
    Network network;
    private final Inventory inventory;
    private final ArrayList<ArrayList<ItemStack>> contents = new ArrayList<>();
    private int page;
    public InventoryMenu(Player player, Network network) {

        this.player = player;
        this.network = network;

        inventory = Bukkit.createInventory(player, 54, "Content of network " + network.name());
        inventory.setMaxStackSize(127);

        updateInventory();
        renderInventory();
        player.openInventory(inventory);
    }

    public Inventory getInventory() {return inventory;}
    public void updateInventory() {
        int slot = 0;

        ArrayList<ItemStack> currentPage = new ArrayList<ItemStack>();
        ArrayList<ItemStack> items = network.items();

        for (int i = 0; i < maxPages()*45; i++) {

            if (i < items.size()-1 ) {
                if (items.get(i) != null) {
                    currentPage.add(items.get(i));
                }
                else {
                    currentPage.add(new ItemStack(Material.AIR));
                }
            }
            else {
                currentPage.add(new ItemStack(Material.AIR));
            }

            slot++;

            if (slot == 45) {

                NamespacedKey key = new NamespacedKey("networks", "menuicon");

                ItemStack first = new ItemStack(Material.SPECTRAL_ARROW);
                ItemMeta itemMeta = first.getItemMeta();
                itemMeta.setDisplayName("§r<<--");
                itemMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
                first.setItemMeta(itemMeta);

                ItemStack back = new ItemStack(Material.ARROW);
                itemMeta = back.getItemMeta();
                itemMeta.setDisplayName("§r<-");
                itemMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 2);
                back.setItemMeta(itemMeta);

                ItemStack empty = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                itemMeta = empty.getItemMeta();
                itemMeta.setDisplayName("  ");
                empty.setItemMeta(itemMeta);

                ItemStack close = new ItemStack(Material.BARRIER);
                itemMeta = close.getItemMeta();
                itemMeta.setDisplayName("§rClose");
                itemMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 0);
                close.setItemMeta(itemMeta);

                ItemStack forward = new ItemStack(Material.ARROW);
                itemMeta = forward.getItemMeta();
                itemMeta.setDisplayName("§r->");
                itemMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 3);
                forward.setItemMeta(itemMeta);

                ItemStack last = new ItemStack(Material.SPECTRAL_ARROW);
                itemMeta = last.getItemMeta();
                itemMeta.setDisplayName("§r-->>");
                itemMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 4);
                last.setItemMeta(itemMeta);



                currentPage.add(first);
                currentPage.add(back);

                currentPage.add(empty);
                currentPage.add(empty);

                currentPage.add(close);

                currentPage.add(empty);
                currentPage.add(empty);

                currentPage.add(forward);
                currentPage.add(last);

                contents.add((ArrayList<ItemStack>) currentPage.clone());


                currentPage.clear();

                slot = 0;
            }
        }
    }

    public void renderInventory() {
        inventory.setContents(contents.get(page).toArray(new ItemStack[54]));
    }

    public void toFirstPage() {
        page = 0;
        renderInventory();
    }

    public void incrementPage() {
        if (page < Math.ceil(network.items().size()/45)) page++;
        renderInventory();
    }

    public void decrementPage() {
        if (page > 0) page--;
        renderInventory();
    }

    public void toLastPage() {
        page = (int) Math.ceil(network.items().size()/45);
        renderInventory();
    }

    private int maxPages() {
        return (int) Math.ceil(network.items().size()/45) + 1;
    }

}
