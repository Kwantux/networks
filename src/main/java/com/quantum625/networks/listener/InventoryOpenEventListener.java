package com.quantum625.networks.listener;

import com.quantum625.networks.NetworkManager;
import com.quantum625.networks.commands.LanguageModule;
import com.quantum625.networks.data.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;


public class InventoryOpenEventListener implements Listener {
    private NetworkManager net;
    private LanguageModule lang;
    private Config config;

    public InventoryOpenEventListener(NetworkManager networkManager, LanguageModule languageModule, Config config) {
        net = networkManager;
        lang = languageModule;
        this.config = config;
    }

    @EventHandler(priority= EventPriority.LOW)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().firstEmpty() == -1) {
            net.noticePlayer((Player) event.getPlayer());
        }
    }
}
