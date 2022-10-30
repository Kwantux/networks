package com.quantum625.networks.listener;

import com.quantum625.networks.NetworkManager;
import com.quantum625.networks.commands.LanguageModule;
import com.quantum625.networks.component.BaseComponent;
import com.quantum625.networks.component.InputContainer;
import com.quantum625.networks.component.MiscContainer;
import com.quantum625.networks.component.SortingContainer;
import com.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

public class NetworkWandListener implements Listener {

    private NetworkManager net;
    private LanguageModule lang;

    public NetworkWandListener(NetworkManager net, LanguageModule languageModule) {
        this.net = net;
        this.lang = languageModule;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (p == null || event.getClickedBlock() == null) return;
        Location l = new Location(event.getClickedBlock());
        Action action = event.getAction();

        if (!p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            if (p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey("networks", "wand"), PersistentDataType.INTEGER)) {
                if (net.getComponentByLocation(l) != null) {
                    if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                        if (p.isSneaking() && !p.getInventory().getItemInOffHand().getType().equals(Material.AIR) && net.getComponentByLocation(l) instanceof SortingContainer) {
                            net.getSortingContainerByLocation(l).addItem(p.getInventory().getItemInOffHand().getType().toString().toUpperCase());
                            lang.returnMessage(p, "component.sorting.setitem", l, p.getInventory().getItemInOffHand().getType());
                        } else {
                            BaseComponent component = net.getComponentByLocation(l);
                            if (component instanceof InputContainer) {
                                lang.returnMessage(p, "info.input", net.getNetworkWithComponent(l), l);
                                event.setCancelled(true);
                            }

                            if (component instanceof SortingContainer) {
                                lang.returnMessage(p, "info.sorting", net.getNetworkWithComponent(l), l, ((SortingContainer) component).getItems());
                                event.setCancelled(true);
                            }

                            if (component instanceof MiscContainer) {
                                lang.returnMessage(p, "info.misc", net.getNetworkWithComponent(l), l);
                                event.setCancelled(true);
                            }
                        }
                    }
                    if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                        if (net.getComponentByLocation(l) instanceof SortingContainer && !p.getInventory().getItemInOffHand().getType().equals(Material.AIR) && p.isSneaking()) {
                            net.getSortingContainerByLocation(l).removeItem(p.getInventory().getItemInOffHand().getType().toString().toUpperCase());
                            lang.returnMessage(p, "component.sorting.removeitem", l, p.getInventory().getItemInOffHand().getType());
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
