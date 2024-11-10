package de.kwantux.networks.event;

import de.kwantux.networks.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static de.kwantux.networks.Main.mgr;
import static de.kwantux.networks.Main.lang;

public class NoticeListener implements Listener {

    private List<UUID> notices = new ArrayList<>();

    public NoticeListener(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryOpen(InventoryCloseEvent event) {
        if (event.getInventory().firstEmpty() != -1) return;
        if (notices.contains(event.getPlayer().getUniqueId())) return;
        if (mgr.withUser(event.getPlayer().getUniqueId()).isEmpty()) {
            notices.add(event.getPlayer().getUniqueId());
            lang.message(event.getPlayer(), "notice");
        }
    }
}
