package net.quantum625.networks.utils;

import net.quantum625.networks.Network;
import org.bukkit.entity.Player;

public class PlayerData {
    private Player player;
    private Network selectedNetwork;


    public PlayerData(Player player) {
        this.player = player;
    }


    public void setNetwork(Network network) {
        selectedNetwork = network;
    }

    public Network getNetwork() {
        return selectedNetwork;
    }

    public Player getPlayer() {
        return player;
    }
}
