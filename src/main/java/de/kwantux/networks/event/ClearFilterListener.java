package de.kwantux.networks.event;

import de.kwantux.networks.Main;
import de.kwantux.networks.component.ComponentType;
import de.kwantux.networks.utils.NamespaceUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ClearFilterListener implements Listener {
    public ClearFilterListener(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

        Player p = event.getPlayer();
        if (!p.isSneaking()) return;

        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType().isBlock()) return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        ComponentType type = ComponentType.get(container.get(NamespaceUtils.COMPONENT.key(), PersistentDataType.STRING));
        if (type != ComponentType.SORTING) return;


        ItemStack newStack = type.item();
        newStack.setAmount(item.getAmount());
        p.getInventory().setItemInMainHand(newStack);
    }
}
