package de.kwantux.networks.event;

import de.kwantux.networks.Main;
import de.kwantux.networks.Manager;
import de.kwantux.networks.Network;
import de.kwantux.networks.component.ComponentType;
import de.kwantux.networks.config.Config;
import de.kwantux.networks.utils.BlockLocation;
import de.kwantux.networks.utils.DoubleChestUtils;
import de.kwantux.networks.utils.NamespaceUtils;
import de.kwantux.config.lang.LanguageController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static de.kwantux.networks.Main.mgr;

public class BlockPlaceListener implements Listener {

    private final Manager net;
    private final Config config;
    private final LanguageController lang;

    private final DoubleChestUtils dcd;

    public BlockPlaceListener (Main plugin, DoubleChestUtils doubleChestDisconnecter) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.net = plugin.getNetworkManager();
        this.config = plugin.getConfiguration();
        this.lang = plugin.getLanguage();

        dcd = doubleChestDisconnecter;
    }

    @EventHandler(priority= EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled()) {

            Player p = event.getPlayer();
            BlockLocation pos = new BlockLocation(event.getBlock());
            Network network = net.selection(p);

            ItemStack item = event.getItemInHand().clone();
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

            ComponentType type = ComponentType.get(container.get(NamespaceUtils.COMPONENT.key(), PersistentDataType.STRING));

            if (type == null) return;

            if (config.checkLocation(pos, type)) {

                if (network == null) {
                    lang.message(p, "select.noselection");
                    event.setCancelled(true);
                    return;
                }

                mgr.createComponent(network, event.getBlock().getType(), type, pos, container);
                lang.message(p, "component."+type.tag+".add", network.name(), pos.toString());
            }
        }
    }

    @EventHandler(priority= EventPriority.LOW)
    public void onComponentInstall(PlayerInteractEvent event) {
        if (!event.isCancelled()) {

            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            if (event.getClickedBlock() == null) return;

            Player p = event.getPlayer();
            BlockLocation pos = new BlockLocation(event.getClickedBlock());
            Network network = net.selection(p);

            ItemStack item = event.getItem();
            if (item == null) return;
            if (item.getType().isBlock()) return;
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

            ComponentType type = ComponentType.get(container.get(NamespaceUtils.COMPONENT.key(), PersistentDataType.STRING));
            if (type == null) return;

            event.setCancelled(true); // At this point, we know that the player is holding a component, so we suppress the default action (chest opens)

            if (mgr.getComponent(pos) != null) { // If there is a component already at the location, we don't install a new one
                lang.message(p, "location.occupied");
                return;
            }

            if (config.checkLocation(pos, type)) { // Check if the block can actually be a component (e.g. grass blocks cannot be input containers, but chests and barrels can)

                if (network == null) {
                    lang.message(p, "select.noselection");
                    event.setCancelled(true);
                    return;
                }

                mgr.createComponent(network, event.getClickedBlock().getType(), type, pos, container);
                item.setAmount(item.getAmount() - 1);
                lang.message(p, "component."+type.tag+".add", network.name(), pos.toString());
            }
        }
    }
}