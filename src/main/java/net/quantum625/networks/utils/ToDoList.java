package net.quantum625.networks.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToDoList {

    public ArrayList<Player> players = new ArrayList<Player>();
    public Map<Material, Integer> list = new HashMap<Material, Integer>();


    public ToDoList() {

    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void setMaterial(Material material, int amount) {
        list.put(material, amount);
        if (amount <= 0) {
            list.remove(material);
        }
    }

    public void removeMaterial(Material material) {
        list.remove(material);
    }
}
