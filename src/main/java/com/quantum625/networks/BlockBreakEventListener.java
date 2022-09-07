package com.quantum625.networks;

import com.quantum625.networks.commands.LanguageModule;
import com.quantum625.networks.component.BaseComponent;
import com.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakEventListener implements Listener {

    private NetworkManager net;
    private LanguageModule lang;

    public BlockBreakEventListener(NetworkManager networkManager, LanguageModule languageModule) {
        net = networkManager;
        lang = languageModule;
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        for (StorageNetwork network : net.listAll()) {
            for (BaseComponent component : network.getAllComponents()) {
                Bukkit.getLogger().info(component.getPos().toString() + " || " + (new Location(event.getBlock())));
                if (component.getPos().equals(new Location(event.getBlock()))) {
                    network.removeComponent(new Location(event.getBlock()));
                    lang.returnMessage(event.getPlayer(), "component.remove");
                }
            }
        }
    }

}
