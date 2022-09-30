package com.quantum625.networks.listener;

import com.quantum625.networks.NetworkManager;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;


public class AutoSave implements Listener {

    private NetworkManager net;

    public AutoSave(NetworkManager net) {
        this.net = net;
    }

    public void onWorldSave(WorldSaveEvent event) {
        net.saveData();
    }

    public void onWorldLoad(WorldLoadEvent event) {
        net.loadData();
    }
}
