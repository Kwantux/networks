package com.quantum625.autosort.utils;

import com.quantum625.autosort.StorageNetwork;
import org.bukkit.entity.Player;

public class PlayerSelection {
    private Player player;
    private StorageNetwork network;


    public PlayerSelection(Player player, StorageNetwork network) {
        this.player = player;
        this.network = network;
    }


    public void setNetwork(StorageNetwork network) {
        this.network = network;
    }

    public StorageNetwork getNetwork() {
        return network;
    }

    public Player getPlayer() {
        return player;
    }
}
