package de.kwantux.networks.event;

import de.kwantux.config.util.exceptions.InvalidNodeException;
import de.kwantux.networks.Main;
import de.kwantux.networks.Network;
import de.kwantux.networks.commands.NetworksCommand;
import de.kwantux.networks.component.BasicComponent;
import de.kwantux.networks.component.component.InputContainer;
import de.kwantux.networks.component.component.SortingContainer;
import de.kwantux.networks.component.module.Acceptor;
import de.kwantux.networks.component.util.FilterTranslator;
import de.kwantux.networks.config.Config;
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
                BasicComponent component = dcu.componentAt(l);
                Network network = null;
                if (component != null) network = dcu.networkWithComponentAt(component.origin());

                if (!p.isSneaking()) {
                    if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                        if (component != null) {
                            boolean isProxy = (network.getComponent(component.origin()) == null);
                            p.sendMessage(NetworksCommand.componentInfo(network, component, isProxy));
                        }
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
                        BasicComponent c = dcu.componentAt(l);
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
                            if (!filters.contains(hash)) {
                                container.addFilter(hash);
                                filters.add(hash);
                            }
                            FilterTranslator.updateTranslation(hash, item.displayName().hoverEvent(HoverEvent.showItem(
                                    HoverEvent.ShowItem.showItem(
                                            Key.key(item.getType().name().toLowerCase()), 1
                                    )
                            )));
                        }
                        lang.message(p, "component.sorting.autofilter", l.displayText());
                    }
                }

                if (action.equals(Action.LEFT_CLICK_BLOCK)) {

                    if ((mode == 0 || mode == 2) && dcu.componentAt(l) instanceof SortingContainer && !itemInOffHand.getType().equals(Material.AIR) && p.isSneaking()) {
                        BasicComponent c = dcu.componentAt(l);
                        if (c instanceof SortingContainer container) {
                            int hash = itemInOffHand.getItemMeta().hashCode();
                            container.removeFilter(itemInOffHand.getType().ordinal());
                            container.removeFilter(hash);
                            lang.message(p, "component.sorting.removeitem", l.displayText(), itemInOffHand.displayName());
                        }
                    }

                    if ((mode == 0 || mode == 2) && dcu.componentAt(l) instanceof SortingContainer container && itemInOffHand.getType().equals(Material.AIR) && p.isSneaking()) {
                        HashSet <Integer> filters = new HashSet<>();
                        for (ItemStack item : container.inventory().getContents()) {
                            // Empty slots are null
                            if (item == null) continue;
                            if (mode == 2) {
                                int hash = item.getItemMeta().hashCode();
                                filters.add(hash);
                                FilterTranslator.updateTranslation(hash, item.displayName().hoverEvent(HoverEvent.showItem(
                                        HoverEvent.ShowItem.showItem(
                                                Key.key(item.getType().name().toLowerCase()), 1
                                        )
                                )));
                            }
                            if (mode == 0) {
                                filters.add(item.getType().ordinal());
                            }
                        }
                        int[] filters_array = new int[filters.size()];
                        int i = 0;
                        for (Integer filter : filters) {
                            filters_array[i] = filter;
                            i++;
                        }
                        container.setFilters(filters_array);
                        lang.message(p, "component.sorting.autofilter", l.displayText());
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
                    BasicComponent component = dcu.componentAt(l);
                    Network network = dcu.networkWithComponentAt(l);
                    if (component == null) {
                        lang.message(p, "component.nocomponent");
                        return;
                    }

                    int tier;
                    Runnable rangeUp;

                    if (Config.rangePerNetwork) {
                        tier = network.rangeTier();
                        rangeUp = () -> network.range(ranges[tier+1]);
                    }
                    else if (component instanceof InputContainer container) {
                        tier = container.range();
                        rangeUp = container::rangeUp;
                    }
                    else {
                        lang.message(p, "rangeupgrade.passivecomponent");
                        return;
                    }

                    int upgradeTier = p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey("networks", "upgrade.range"), PersistentDataType.INTEGER)-1;

                    if (upgradeTier == tier) {
                        ItemStack item = p.getInventory().getItemInMainHand();
                        item.setAmount(item.getAmount() - 1);
                        rangeUp.run();
                        if (Config.rangePerNetwork)
                            lang.message(p, "rangeupgrade.success.network", String.valueOf(tier+1), network.name());
                        else lang.message(p, "rangeupgrade.success", String.valueOf(tier+1), component.origin().toString());
                    }
                    if (tier == ranges.length) {
                        lang.message(p, "rangeupgrade.last");
                        return;
                    }
                    if (tier > ranges.length || tier < 0) {
                        lang.message(p, "rangeupgrade.invalid");
                        return;
                    }
                    if (upgradeTier < tier) {
                        lang.message(p, "rangeupgrade.alreadyupgraded", String.valueOf(tier));
                    }
                    if (upgradeTier > tier) {
                        lang.message(p, "rangeupgrade.unlockfirst", String.valueOf(tier+1));
                    }


                }
            }
        }
    }
}