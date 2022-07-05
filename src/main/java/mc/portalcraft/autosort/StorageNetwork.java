package mc.portalcraft.autosort;

import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.*;

import java.util.ArrayList;
import java.util.UUID;

public class StorageNetwork {
    private String id;
    private UUID owner;

    private ArrayList<Location> input_chests = new ArrayList<Location>();
    private ArrayList<Location> sorting_chests = new ArrayList<Location>();
    private ArrayList<Location> misc_chests = new ArrayList<Location>();



    public StorageNetwork(String id, UUID owner) {
        this.id = id;
        this.owner = owner;
    }

    public String getID() {
        return this.id;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public void addInputChest(Location pos) {
        input_chests.add(pos);
    }

    public void addItemChest(Location pos) {
        sorting_chests.add(pos);
    }

    public void addMiscChest(Location pos) {
        misc_chests.add(pos);
    }



    public void sortAll() {
        for (int i = 0; i < input_chests.size()-1; i++) {
            sort(input_chests.get(i));
        }
    }

    public void sort(Location pos) {
        if (input_chests.contains(pos)) {
            Block b = pos.getBlock();

            if (b.getType() == Material.CHEST) {

                // Main body

            }

            else {
                Bukkit.getLogger().warning("Block at the given position is not a chest: " + pos.toString());
            }
        }

        else {
            Bukkit.getLogger().warning("Network contains no chest at the given position: " + pos.toString());
        }
    }


}
