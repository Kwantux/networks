package com.quantum625.autosort;

import com.quantum625.autosort.container.InputContainer;
import com.quantum625.autosort.container.ItemContainer;
import com.quantum625.autosort.container.MiscContainer;
import com.quantum625.autosort.data.Network;
import com.quantum625.autosort.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class StorageNetwork {
    private String id;
    private UUID owner;

    private ArrayList<InputContainer> input_containers = new ArrayList<InputContainer>();
    private ArrayList<ItemContainer> sorting_containers = new ArrayList<ItemContainer>();
    private ArrayList<MiscContainer> misc_containers = new ArrayList<MiscContainer>();



    public StorageNetwork(String id, UUID owner) {
        this.id = id;
        this.owner = owner;
    }

    public StorageNetwork(Network net) {
        this.id = net.getId();
        this.owner = UUID.fromString(net.getOwner());

        for (InputContainer i: net.getInputContainers()) {
            input_containers.add(i);
        }

        for (ItemContainer i: net.getSortingContainers()) {
            sorting_containers.add(i);
        }

        for (MiscContainer i: net.getMiscContainers()) {
            misc_containers.add(i);
        }
    }

    private InputContainer getInputContainerByLocation(Location pos) {
        for (InputContainer i : input_containers) {
            if (i.getPos().equals(pos)) {
                return i;
            }
        }
        return null;
    }
    private ItemContainer getItemContainerByLocation(Location pos) {
        for (ItemContainer i : sorting_containers) {
            if (i.getPos().equals(pos)) {
                return i;
            }
        }
        return null;
    }


    // Unfinished:
    private ItemContainer getItemContainerByItem(String item) {
        for (ItemContainer i : sorting_containers) {
            if (i.getItem().equals(item) && i.getInventory().contains(Material.AIR)) {
                return i;
            }
        }
        return null;
    }


    private MiscContainer getMiscContainerByLocation(Location pos) {
        for (MiscContainer i : misc_containers) {
            if (i.getPos().equals(pos)) {
                return i;
            }
        }
        return null;
    }

    private MiscContainer getMiscContainer() {
        for (MiscContainer i : misc_containers) {
            if (i.getInventory().contains(Material.AIR)) {
                return i;
            }
        }
        return null;
    }



    public String getID() {
        return this.id;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public ArrayList<InputContainer> getInputChests() {
        return input_containers;
    }

    public ArrayList<ItemContainer> getSortingChests() {
        return sorting_containers;
    }

    public ArrayList<MiscContainer> getMiscChests() {
        return misc_containers;
    }


    public void addInputChest(Location pos) {
        input_containers.add(new InputContainer(pos, 20));
    }

    public void addItemChest(Location pos, String item) {
        sorting_containers.add(new ItemContainer(pos, item));
    }

    public void addMiscChest(Location pos, boolean takeOverflow) {
        misc_containers.add(new MiscContainer(pos, takeOverflow));
    }



    public void sortAll() {
        for (int i = 0; i < input_containers.size()-1; i++) {
            sort(input_containers.get(i).getPos());
        }
    }

    public void sort(Location pos) {
        if (getInputContainerByLocation(pos) != null) {

            Inventory inventory = getInputContainerByLocation(pos).getInventory();

            for (ItemStack stack : inventory.getContents()) {

                if (getItemContainerByItem(stack.getType().toString().toUpperCase()) != null) {
                    getItemContainerByItem(stack.getType().toString().toUpperCase()).getInventory().addItem(stack);
                }

                else {
                    getMiscContainer().getInventory().addItem(stack);
                }

                inventory.remove(stack);
            }
        }

        else {
            Bukkit.getLogger().warning("Network contains no chest at the given position: " + pos.toString());
        }
    }


}
