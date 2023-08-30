package net.quantum625.networks.listener;

import net.quantum625.networks.inventory.InventoryMenu;
import net.quantum625.networks.inventory.InventoryMenuManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.persistence.PersistentDataType;

public class InventoryMenuListener implements Listener {

    public InventoryMenuListener() {

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClicked(InventoryClickEvent event) {
        if (InventoryMenuManager.isInventoryMenu(event.getInventory())) {

            Player player = (Player) event.getWhoClicked();
            InventoryMenu menu = InventoryMenuManager.getMenuForPlayer(player);

            event.setCancelled(true);
            if (event.getCurrentItem().getItemMeta() == null) return;
            if (event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey("networks", "menuicon"), PersistentDataType.INTEGER)) {
                switch (event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey("networks", "menuicon"), PersistentDataType.INTEGER)) {
                    case 0:
                        player.closeInventory();
                        break;

                    case 1:
                        menu.toFirstPage();
                        break;

                    case 2:
                        menu.decrementPage();
                        break;

                    case 3:
                        menu.incrementPage();
                        break;

                    case 4:
                        menu.toLastPage();
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (InventoryMenuManager.isInventoryMenu(event.getInventory())) {
            InventoryMenuManager.removeInventoryMenu((Player) event.getPlayer());
        }
    }
}
