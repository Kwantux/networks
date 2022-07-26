package mc.portalcraft.autosort;

import mc.portalcraft.autosort.container.InputContainer;
import mc.portalcraft.autosort.container.ItemContainer;
import mc.portalcraft.autosort.container.MiscContainer;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.*;

import java.util.ArrayList;
import java.util.UUID;

public class StorageNetwork {
    private String id;
    private UUID owner;

    private ArrayList<InputContainer> input_chests = new ArrayList<InputContainer>();
    private ArrayList<ItemContainer> sorting_chests = new ArrayList<ItemContainer>();
    private ArrayList<MiscContainer> misc_chests = new ArrayList<MiscContainer>();



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

    public ArrayList<InputContainer> getInputChests() {
        return input_chests;
    }

    public ArrayList<ItemContainer> getSortingChests() {
        return sorting_chests;
    }

    public ArrayList<MiscContainer> getMiscChests() {
        return misc_chests;
    }


    public void addInputChest(Location pos) {
        input_chests.add(new InputContainer(pos));
    }

    public void addItemChest(Location pos, Material item) {
        sorting_chests.add(new ItemContainer(pos, item));
    }

    public void addMiscChest(Location pos) {
        misc_chests.add(new MiscContainer(pos));
    }



    public void sortAll() {
        for (int i = 0; i < input_chests.size()-1; i++) {
            sort(input_chests.get(i).getPos());
        }
    }

    public void sort(Location pos) {
        if (input_chests.contains(pos)) {
            Block b = pos.getBlock();

            if (b.getType() == Material.CHEST) {

                // Main body

            }

            else {
                Bukkit.getLogger().warning("Block at the given position is not a chest: " + pos);
            }
        }

        else {
            Bukkit.getLogger().warning("Network contains no chest at the given position: " + pos.toString());
        }
    }


}
