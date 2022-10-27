package com.quantum625.networks.listener;

import com.quantum625.networks.Network;
import com.quantum625.networks.NetworkManager;
import com.quantum625.networks.commands.LanguageModule;
import com.quantum625.networks.data.Config;
import com.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class BlockPlaceEventListener implements Listener {

    private final NetworkManager net;
    private final Config config;
    private final LanguageModule lang;


    public BlockPlaceEventListener (NetworkManager net, Config config, LanguageModule lang) {
        this.net = net;
        this.config = config;
        this.lang = lang;
    }

    @EventHandler(priority= EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        // Economy State defines whether components are bought with Vault money or by crafting them.
        if (!event.isCancelled() && !config.getEconomyState()) {

            Player p = event.getPlayer();
            Location pos = new Location(event.getBlock());
            Network network = net.getSelectedNetwork(p);

            if (config.checkLocation(pos, "container")) {

                if (net.getSelectedNetwork(p) != null) {

                    Bukkit.getLogger().info(event.getItemInHand().getItemMeta().getAsString());
                    ItemStack item = event.getItemInHand().clone();
                    item.getItemMeta().setDisplayName(" ");
                    item.getItemMeta().setLore(Arrays.asList());
                    Bukkit.getLogger().info("Keys: " + item.getItemMeta().getPersistentDataContainer().getKeys());

                    if (item.getItemMeta().getAsString().contains("component_type:\"input\"")) {
                        net.getSelectedNetwork(p).addInputContainer(pos);
                        net.selectComponentType(p, null);
                        lang.returnMessage(p, "component.input.add", network, pos);
                    }

                    if (item.getItemMeta().getAsString().contains("component_type:\"item\"")) {

                        String[] items = item.getItemMeta().getAsString().split("\n");

                        net.getSelectedNetwork(p).addItemContainer(pos, items);
                        net.selectComponentType(p, null);
                        lang.returnMessage(p, "component.item.add", network, pos);
                    }

                    if (item.getItemMeta().getAsString().contains("component_type:\"misc\"")) {
                        net.getSelectedNetwork(p).addMiscContainer(pos);
                        net.selectComponentType(p, null);
                        lang.returnMessage(p, "component.misc.add", network, pos);
                    }

                }

                else {
                    lang.returnMessage(p, "select.noselection");
                    event.setCancelled(true);
                }

            }
        }
    }
}
// TODO: 26.10.22 test this method and remove it when   it is implemented