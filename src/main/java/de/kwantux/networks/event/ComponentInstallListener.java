package de.kwantux.networks.event;

import de.kwantux.networks.Main;
import de.kwantux.networks.Network;
import de.kwantux.networks.component.util.ComponentType;
import de.kwantux.networks.utils.BlockLocation;
import de.kwantux.networks.utils.NamespaceUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static de.kwantux.networks.Main.*;

public class ComponentInstallListener implements Listener {

    public ComponentInstallListener(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onComponentInstall(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        if (item.getType().isBlock()) return; // This case is handled by the BlockPlaceEvent
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        ComponentType type = ComponentType.get(container.get(NamespaceUtils.COMPONENT.key, PersistentDataType.STRING));
        if (type == null) return;

        Player p = event.getPlayer();
        BlockLocation pos = new BlockLocation(event.getClickedBlock());
        Network network = mgr.selection(p);

        event.setCancelled(true); // At this point, we know that the player is holding a component, so we suppress the default action (chest opens)

        if (mgr.getComponent(pos) != null) { // If there is a component already at the location, we don't install a new one
            lang.message(p, "location.occupied");
            return;
        }

        if (cfg.checkLocation(pos, type)) { // Check if the block can actually be a component (e.g. grass blocks cannot be input containers, but chests and barrels can)

            if (network == null) {
                lang.message(p, "select.noselection");
                event.setCancelled(true);
                return;
            }

            mgr.createComponent(network, type, pos, container);
            item.setAmount(item.getAmount() - 1);
            lang.message(p, "component."+type.tag+".add", network.name(), pos.toString());
        }

        else
            lang.message(p, "component.invalid_block", Component.translatable(pos.getBlock().getType().translationKey()));
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onComponentInstall(BlockPlaceEvent event) {
        if (!event.isCancelled()) {

            ItemStack item = event.getItemInHand();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;
            PersistentDataContainer container = meta.getPersistentDataContainer();

            ComponentType type = ComponentType.get(container.get(NamespaceUtils.COMPONENT.key, PersistentDataType.STRING));
            if (type == null) return;

            Player p = event.getPlayer();
            BlockLocation pos = new BlockLocation(event.getBlock());
            Network network = mgr.selection(p);

            if (network == null) {
                lang.message(p, "select.noselection");
                event.setCancelled(true);
                return;
            }

            mgr.createComponent(network, type, pos, container);
            lang.message(p, "component."+type.tag+".add", network.name(), pos.toString());
        }
    }
}