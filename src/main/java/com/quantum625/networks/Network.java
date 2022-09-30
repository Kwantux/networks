package com.quantum625.networks;

import com.quantum625.networks.component.BaseComponent;
import com.quantum625.networks.component.InputContainer;
import com.quantum625.networks.component.ItemContainer;
import com.quantum625.networks.component.MiscContainer;
import com.quantum625.networks.data.JSONNetwork;
import com.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class Network {
    private String id;

    private UUID owner;
    private ArrayList<UUID> admins = new ArrayList<UUID>();
    private ArrayList<UUID> users = new ArrayList<UUID>();
    private int maxContainers = 20;
    private int maxRange = 40;

    private ArrayList<InputContainer> input_containers = new ArrayList<InputContainer>();
    private ArrayList<ItemContainer> sorting_containers = new ArrayList<ItemContainer>();
    private ArrayList<MiscContainer> misc_containers = new ArrayList<MiscContainer>();



    public Network(String id, UUID owner, Location center, int maxContainers, int maxRange) {
        this.id = id;
        this.owner = owner;
        this.maxContainers = maxContainers;
        this.maxRange = maxRange;
    }

    public Network(JSONNetwork net) {
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

    // Unfinished:
    private ItemContainer getItemContainerByItem(Location pos, String item) {
        for (ItemContainer i : sorting_containers) {
            if (i.getItem().equals(item) && i.getInventory().firstEmpty() != -1 && i.getPos().getDistance(pos) <= maxRange) {
                return i;
            }
        }
        return null;
    }


    private MiscContainer getMiscContainer(Location pos) {
        for (MiscContainer i : misc_containers) {
            if (i.getInventory().firstEmpty() != -1 && i.getPos().getDistance(pos) <= maxRange) {
                return i;
            }
        }
        return null;
    }

    public BaseComponent getComponentByLocation(Location pos) {
        for (BaseComponent component : getAllComponents()) {
            if (component.getPos().equals(pos)) {
                return component;
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

    public ArrayList<UUID> getAdmins() {return admins;}
    public ArrayList<UUID> getUsers() {return users;}
    public int getMaxContainers() {return maxContainers;}
    public int getMaxRange() {return maxRange;}

    public ArrayList<BaseComponent> getAllComponents() {
        ArrayList<BaseComponent> components = new ArrayList<BaseComponent>();
        components.addAll(this.input_containers);
        components.addAll(this.sorting_containers);
        components.addAll(this.misc_containers);
        return components;
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


    public boolean checkContainerLimit() {
        return getAllComponents().size() < maxContainers;
    }
    public void addInputContainer(Location pos) {
        input_containers.add(new InputContainer(pos));
    }

    public void addItemContainer(Location pos, String item) {
        sorting_containers.add(new ItemContainer(pos, item));
    }

    public void addMiscContainer(Location pos) {
        misc_containers.add(new MiscContainer(pos));
    }

    public void removeComponent(Location location) {
        for (int i = 0; i < input_containers.size(); i++) {
            if (input_containers.get(i).getPos().equals(location)) {
                input_containers.remove(i);
            }
        }
        for (int i = 0; i < sorting_containers.size(); i++) {
            if (sorting_containers.get(i).getPos().equals(location)) {
                sorting_containers.remove(i);
            }
        }
        for (int i = 0; i < misc_containers.size(); i++) {
            if (misc_containers.get(i).getPos().equals(location)) {
                misc_containers.remove(i);
            }
        }
    }

    public void upgradeLimit(int amount) {
        maxContainers += amount;
    }

    public void upgradeRange(int amount) {
        maxRange += amount;
    }


    public void sortAll() {
        for (int i = 0; i < input_containers.size(); i++) {
            sort(input_containers.get(i).getPos());
        }
    }

    public void sort(Location pos) {
        if (getInputContainerByLocation(pos) != null) {

            Inventory inventory = getInputContainerByLocation(pos).getInventory();

            if (inventory == null) {
                Bukkit.getLogger().warning("Inventory of block at position " + pos.toString() + " is null");
                return;
            }


            for (ItemStack stack : inventory.getContents()) {

                if (stack != null) {

                    Bukkit.getLogger().info("ItemStack " + stack.getType());

                    ItemContainer container = getItemContainerByItem(pos, stack.getType().toString().toUpperCase());
                    if (container != null) {
                        container.getInventory().addItem(stack);
                        inventory.remove(stack);
                        Bukkit.getLogger().info("Item moved to item container");
                    }

                    else {
                        MiscContainer miscContainer = getMiscContainer(pos);
                        if (miscContainer != null) {
                            miscContainer.getInventory().addItem(stack);
                            inventory.remove(stack);
                            Bukkit.getLogger().info("Item moved to misc container");
                        }
                    }
                }
            }
        }

        else {
            Bukkit.getLogger().warning("JSONNetwork contains no chest at the given position: " + pos.toString());
        }
    }


}
