package com.quantum625.networks.listener;

import com.quantum625.networks.NetworkManager;
import com.quantum625.networks.commands.LanguageModule;
import com.quantum625.networks.component.ItemContainer;
import com.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        if (!p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            if (p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey("networks", "wand"), PersistentDataType.INTEGER)) {
                Bukkit.getLogger().info("onPlayerInteract");
                if (net.getComponentByLocation(new Location(event.getClickedBlock().getLocation())) != null) {
                    if (net.getComponentByLocation(new Location(event.getClickedBlock().getLocation())) instanceof ItemContainer && !p.getInventory().getItemInOffHand().getType().equals(Material.AIR) && p.isSneaking()) {
                        net.getSortingContainerByLocation(new Location(event.getClickedBlock().getLocation())).addItem(p.getInventory().getItemInOffHand().getType().toString().toUpperCase());
                        lang.returnMessage(p, "component.sorting.setitem", p.getInventory().getItemInOffHand().getType());
                    }
                }
            }
        }
    }
}
