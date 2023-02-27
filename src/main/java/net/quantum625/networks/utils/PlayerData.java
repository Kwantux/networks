package net.quantum625.networks.utils;

import net.quantum625.networks.Network;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class PlayerData {
    private Player player;

    private Network selectedNetwork;
    private Location selectedLocation;
    private String selectedComponentType;
    private String[] selectedItems;


    public PlayerData(Player player) {
        this.player = player;
    }


    public void setNetwork(Network network) {
        selectedNetwork = network;
    }

    public Network getNetwork() {
        return selectedNetwork;
    }


    public void setComponentType(@Nullable String componentType) {
        selectedComponentType = componentType;
    }
    public String getComponentType() {return selectedComponentType;}


    public void setItems(@Nullable String[] items) {selectedItems = items;}
    public String[] getItems() {return selectedItems;}


    public void setLocation(@Nullable Location location) {
        this.selectedLocation = location;
    }

    public Location getLocation() {
        return selectedLocation;
    }

    public Player getPlayer() {
        return player;
    }
}
