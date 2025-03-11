package de.kwantux.networks.event;

import de.kwantux.config.util.exceptions.InvalidNodeException;
import de.kwantux.networks.Main;
import de.kwantux.networks.Network;
import de.kwantux.networks.commands.NetworksCommand;
import de.kwantux.networks.component.NetworkComponent;
import de.kwantux.networks.component.component.SortingContainer;
import de.kwantux.networks.component.module.Acceptor;
import de.kwantux.networks.component.module.Donator;
import de.kwantux.networks.component.module.Requestor;
import de.kwantux.networks.component.util.FilterTranslator;
import de.kwantux.networks.utils.BlockLocation;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;

import static de.kwantux.networks.Main.*;
import static de.kwantux.networks.config.Config.ranges;

public class WandListener implements Listener {

    public WandListener(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) throws InvalidNodeException {
        Player p = event.getPlayer();
        BlockLocation l = null;
        if (event.getClickedBlock() != null) l = new BlockLocation(event.getClickedBlock());
        Action action = event.getAction();

        ItemStack wand = p.getInventory().getItemInMainHand();

        if (!wand.getType().equals(Material.AIR)) {
            if (wand.getItemMeta().getPersistentDataContainer().has(new NamespacedKey("networks", "wand"), PersistentDataType.INTEGER)) {

                event.setCancelled(true);

                if (!event.getHand().equals(EquipmentSlot.HAND)) return;

                int mode = wand.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("networks", "wand"), PersistentDataType.INTEGER);

                if (!p.isSneaking()) {
                    if (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR)) {
                        if (!p.isSneaking()) {
                            mode++;
                            if (mode > 2) mode = 0;
                            //p.getInventory().setItemInMainHand(crf.getNetworkWand(mode));
                            event.getItem().setItemMeta(crf.getNetworkWand(mode).getItemMeta());
                            lang.message(p, "wand.mode", lang.getRaw("wand.mode." + mode));
                            return;
                        }
                    }
                }

                if (l == null) return;
                NetworkComponent component = dcu.componentAt(l);
                Network network = null;
                if (component != null) network = dcu.networkWithComponentAt(component.pos());

                if (!p.isSneaking()) {
                    if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                        if (component != null)
                            p.sendMessage(NetworksCommand.componentInfo(network, component));
                        else
                            lang.message(p, "component.nocomponent");
                    }
                    return;
                }

                if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_AIR)) {
                    return;
                }

                if (component == null) {
                    lang.message(p, "component.nocomponent");
                    return;
                }

                if (!mgr.permissionUser(p, network)) {
                    lang.message(p, "permission.user");
                    return;
                }

                ItemStack itemInOffHand = p.getInventory().getItemInOffHand();
                if (action.equals(Action.RIGHT_CLICK_BLOCK)) {

                    if (mode == 0 && !itemInOffHand.getType().equals(Material.AIR) && dcu.componentAt(l) instanceof SortingContainer) {
                        NetworkComponent c = dcu.componentAt(l);
                        if (c instanceof SortingContainer container) {
                            container.addFilter(itemInOffHand.getType().ordinal());
                            lang.message(p, "component.sorting.setitem", l.toString(), itemInOffHand.getType().toString());
                        }
                    }
                    // If in filter mode and no item in offhand, add contents to container's filter
                    if (mode == 0 && itemInOffHand.getType().equals(Material.AIR) && component instanceof SortingContainer container) {
                        HashSet<Integer> filters = new HashSet<>();
                        for (int num : container.filters()) {
                            filters.add(num);
                        }

                        for (ItemStack item : container.inventory().getContents()) {
                            // Empty slots are null
                            if (item == null) continue;
                            Integer itemType = item.getType().ordinal();
                            if (!filters.contains(itemType)) {
                                container.addFilter(itemType);
                                filters.add(itemType);
                            }
                        }
                        lang.message(p, "component.sorting.autofilter", l.toString());
                    }
                    if (mode == 1) {
                        if (component instanceof Acceptor container) {
                            container.incrementAcceptorPriority();
                            lang.message(p, "component.priority", String.valueOf(container.acceptorPriority()));
                        }
                    }
                    if (mode == 2 && !itemInOffHand.getType().equals(Material.AIR) && dcu.componentAt(l) instanceof SortingContainer container) {
                        int hash = itemInOffHand.getItemMeta().hashCode();
                        container.addFilter(hash);
                        FilterTranslator.updateTranslation(hash, itemInOffHand.displayName().hoverEvent(HoverEvent.showItem(
                                HoverEvent.ShowItem.showItem(
                                        Key.key(itemInOffHand.getType().name().toLowerCase()), 1
                                )
                        )));
                        lang.message(p, "component.sorting.setitem", l.displayText(), itemInOffHand.displayName());
                    }
                    // If in filter mode and no item in offhand, add contents to container's filter
                    if (mode == 2 && itemInOffHand.getType().equals(Material.AIR) && component instanceof SortingContainer container) {
                        HashSet<Integer> filters = new HashSet<>();
                        for (int num : container.filters()) {
                            filters.add(num);
                        }

                        for (ItemStack item : container.inventory().getContents()) {
                            // Empty slots are null
                            if (item == null) continue;
                            int hash = item.getItemMeta().hashCode();
                            container.addFilter(hash);
                            FilterTranslator.updateTranslation(hash, item.displayName().hoverEvent(HoverEvent.showItem(
                                    HoverEvent.ShowItem.showItem(
                                            Key.key(item.getType().name().toLowerCase()), 1
                                    )
                            )));
                        }
                        lang.message(p, "component.sorting.autofilter", l.toString());
                    }
                }

                if (action.equals(Action.LEFT_CLICK_BLOCK)) {

                    if ((mode == 0 || mode == 2) && dcu.componentAt(l) instanceof SortingContainer && !itemInOffHand.getType().equals(Material.AIR) && p.isSneaking()) {
                        NetworkComponent c = dcu.componentAt(l);
                        if (c instanceof SortingContainer container) {
                            int hash = itemInOffHand.getItemMeta().hashCode();
                            container.removeFilter(itemInOffHand.getType().ordinal());
                            container.removeFilter(hash);
                            lang.message(p, "component.sorting.removeitem", l.displayText(), itemInOffHand.displayName());
                        }
                    }

                    if (mode == 1) {
                        if (component instanceof Acceptor container) {
                            container.decrementAcceptorPriority();
                            lang.message(p, "component.priority", String.valueOf(container.acceptorPriority()));
                        }
                    }
                }
            }

            if (p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey("networks", "upgrade.range"), PersistentDataType.INTEGER)) {
                if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                    event.setCancelled(true);
                    NetworkComponent component = dcu.componentAt(l);
                    if (component == null) {
                        lang.message(p, "component.nocomponent");
                        return;
                    }
                    if (component instanceof Donator donator) {
                        int tier = donator.range();
                        int upgradeTier = p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey("networks", "upgrade.range"), PersistentDataType.INTEGER)-1;

                        if (upgradeTier == tier) {
                            ItemStack item = p.getInventory().getItemInMainHand();
                            item.setAmount(item.getAmount() - 1);
                            donator.rangeUp();
                            lang.message(p, "rangeupgrade.success", String.valueOf(tier+1), component.pos().toString());
                        }
                        if (tier == ranges.length) {
                            lang.message(p, "rangeupgrade.last");
                            return;
                        }
                        if (upgradeTier < tier) {
                            lang.message(p, "rangeupgrade.alreadyupgraded", String.valueOf(tier));
                        }
                        if (upgradeTier > tier) {
                            lang.message(p, "rangeupgrade.unlockfirst", String.valueOf(tier));
                        }
                    }
                    else if (component instanceof Requestor requestor) {
                        int tier = requestor.range();
                        int upgradeTier = p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey("networks", "upgrade.range"), PersistentDataType.INTEGER);

                        if (upgradeTier == tier) {
                            ItemStack item = p.getInventory().getItemInMainHand();
                            item.setAmount(item.getAmount() - 1);
                            requestor.rangeUp();
                            lang.message(p, "rangeupgrade.success", String.valueOf(tier), component.pos().toString());
                        }
                        if (tier == ranges.length) {
                            lang.message(p, "rangeupgrade.last");
                            return;
                        }
                        if (upgradeTier < tier) {
                            lang.message(p, "rangeupgrade.alreadyupgraded", String.valueOf(tier));
                        }
                        if (upgradeTier > tier) {
                            lang.message(p, "rangeupgrade.unlockfirst", String.valueOf(tier));
                        }
                    }
                    else {
                        lang.message(p, "rangeupgrade.passivecomponent");
                    }

                    
                }
            }
        }
    }
}