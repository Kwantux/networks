package net.quantum625.networks.listener;

import net.quantum625.networks.Main;
import net.quantum625.networks.NetworkManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;


public class AutoSave implements Listener {

    private NetworkManager net;

    public AutoSave(Main main) {
        this.net = main.getNetworkManager();
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        net.saveData();
    }

    /*
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        net.loadData();
    }
    */
}
