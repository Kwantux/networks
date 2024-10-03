package dev.nanoflux.networks.event;

import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.utils.NamespaceUtils;
import dev.nanoflux.config.lang.LanguageController;
import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Network;
import dev.nanoflux.networks.Manager;
import dev.nanoflux.networks.config.Config;
import dev.nanoflux.networks.utils.DoubleChestUtils;
import dev.nanoflux.networks.utils.BlockLocation;
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

import static dev.nanoflux.networks.Main.mgr;

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
            if (item.getType().isBlock()) return; // This is handeled by the function above
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

            ComponentType type = ComponentType.get(container.get(NamespaceUtils.COMPONENT.key(), PersistentDataType.STRING));

            if (type == null) return;

            if (config.checkLocation(pos, type)) {

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