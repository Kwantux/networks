package dev.nanoflux.networks.inventory;

import dev.nanoflux.networks.Network;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class InventoryMenuManager {

    static private final ArrayList<InventoryMenu> list = new ArrayList<>();


    static public void updateMenus() {
        list.forEach(inventoryMenu -> {
           inventoryMenu.updateInventory();
        });
    }

    static public void addInventoryMenu(Player player, Network network) {
        list.add(new InventoryMenu(player, network));
    }

    static public void removeInventoryMenu(Player player) {

        InventoryMenu menu = getMenuForPlayer(player);

        if (menu != null) {
            list.remove(menu);
        }
    }

    static public InventoryMenu getMenuForPlayer(Player player) {
        for (InventoryMenu inventoryMenu : list) {
            if (inventoryMenu.player.equals(player)) {
                return inventoryMenu;
            }
        }
        return null;
    }

    static public void closeAll() {
        for (InventoryMenu menu : list) {
            menu.player.closeInventory();
        }
    }

    static public ArrayList<InventoryMenu> listInventoryMenus() {
        return list;
    }


    static public boolean isInventoryMenu(Inventory inventory) {
        for (InventoryMenu menu : list) {
            if (menu.getInventory().equals(inventory)) return true;
        }
        return false;
    }
}
