package com.quantum625.networks.listener;

import com.quantum625.networks.NetworkManager;
import com.quantum625.networks.Network;
import com.quantum625.networks.commands.LanguageModule;
import com.quantum625.networks.component.BaseComponent;
import com.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakEventListener implements Listener {

    private NetworkManager net;
    private LanguageModule lang;

    public BlockBreakEventListener(NetworkManager networkManager, LanguageModule languageModule) {
        net = networkManager;
        lang = languageModule;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void blockBreak(BlockBreakEvent event) {
        for (Network network : net.listAll()) {
            for (BaseComponent component : network.getAllComponents()) {
                if (component.getPos().equals(new Location(event.getBlock()))) {
                    if (net.checkNetworkPermission(event.getPlayer(), network) > 1) {
                        network.removeComponent(new Location(event.getBlock()));
                        lang.returnMessage(event.getPlayer(), "component.remove", new Location(event.getBlock()));
                    }
                    else {
                        lang.returnMessage(event.getPlayer(), "permission.admin");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
