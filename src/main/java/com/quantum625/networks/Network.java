package com.quantum625.networks;

import com.quantum625.networks.component.BaseComponent;
import com.quantum625.networks.component.InputContainer;
import com.quantum625.networks.component.SortingContainer;
import com.quantum625.networks.component.MiscContainer;
import com.quantum625.networks.data.JSONNetwork;
import com.quantum625.networks.utils.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Network {
    private String id;

    private UUID owner;
    private ArrayList<UUID> users = new ArrayList<UUID>();
    private int maxContainers = 20;
    private int maxRange = 40;

    private int sorting_counter = 0;

    private ArrayList<InputContainer> input_containers = new ArrayList<InputContainer>();
    private ArrayList<SortingContainer> sorting_containers = new ArrayList<SortingContainer>();
    private ArrayList<MiscContainer> misc_containers = new ArrayList<MiscContainer>();



    public Network(String id, UUID owner, int maxContainers, int maxRange) {
        this.id = id;
        this.owner = owner;
        this.maxContainers = maxContainers;
        this.maxRange = maxRange;
    }

    public Network(JSONNetwork net) {
        this.id = net.getId();
        this.owner = UUID.fromString(net.getOwner());

        this.users = net.getUsers();

        this.maxContainers = net.getMaxContainers();
        this.maxRange = net.getMaxRange();

        for (InputContainer i: net.getInputContainers()) {
            input_containers.add(i);
        }

        for (SortingContainer i: net.getSortingContainers()) {
            sorting_containers.add(i);
        }

        for (MiscContainer i: net.getMiscContainers()) {
            misc_containers.add(i);
        }
    }

    public InputContainer getInputContainerByLocation(Location pos) {
        for (InputContainer i : input_containers) {
            if (i.getPos().equals(pos)) {
                return i;
            }
        }
        return null;
    }

    public SortingContainer getSortingContainerByLocation(Location pos) {
        for (SortingContainer i : sorting_containers) {
            if (i.getPos().equals(pos)) {
                return i;
            }
        }
        return null;
    }

    private SortingContainer getItemContainerByItem(Location pos, String item) {
        for (SortingContainer i : sorting_containers) {
            if (Arrays.stream(i.getItems()).toList().contains(item) && i.getInventory().firstEmpty() != -1 && i.getPos().getDistance(pos) <= maxRange) {
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


    public void merge(Network other) {
        if (other != null) {
            if (other.getInputChests().size() > 0) {
                for (InputContainer inputContainer : other.getInputChests()) {
                    addInputContainer(inputContainer.getPos());
                }
            }
            if (other.getSortingChests().size() > 0) {
                for (SortingContainer sortingContainer : other.getSortingChests()) {
                    addItemContainer(sortingContainer.getPos(), sortingContainer.getItems());
                }
            }
            if (other.getMiscChests().size() > 0) {
                for (MiscContainer miscContainer : other.getMiscChests()) {
                    addMiscContainer(miscContainer.getPos());
                }
            }

            maxContainers +=  other.getMaxContainers();
            //maxRange += other.getMaxRange();

            if (other.getUsers().size() > 0) {
                for (UUID user : other.getUsers()) {
                    if (!getUsers().contains(user)) {
                        addUser(user);
                    }
                }
            }
        }
    }

    public int getSortingCounter() {return sorting_counter;}


    public String getID() {
        return this.id;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public ArrayList<UUID> getUsers() {return users;}

    public void addUser(UUID player) {
        users.add(player);
    }
    public void removeUser(UUID player) {
        users.remove(player);
    }

    public int getMaxContainers() {return maxContainers;}
    public int getMaxRange() {return maxRange;}
    public void setMaxRange(int maxRange) {this.maxRange = maxRange;}

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

    public ArrayList<SortingContainer> getSortingChests() {
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

    public void addItemContainer(Location pos, String[] items) {
        sorting_containers.add(new SortingContainer(pos, items));
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
                return;
            }


            for (ItemStack stack : inventory.getContents()) {

                if (stack != null) {

                    sorting_counter += 1;

                    SortingContainer container = getItemContainerByItem(pos, stack.getType().toString().toUpperCase());
                    if (container != null) {
                        container.getInventory().addItem(stack);
                        inventory.remove(stack);
                    }

                    else {
                        MiscContainer miscContainer = getMiscContainer(pos);
                        if (miscContainer != null) {
                            miscContainer.getInventory().addItem(stack);
                            inventory.remove(stack);
                        }
                    }
                }
            }
        }
    }

    public ArrayList<ItemStack> getItems() {
        ArrayList<ItemStack> result = new ArrayList<ItemStack>();
        for (BaseComponent component : getAllComponents()) {
            result.addAll(Arrays.stream(component.getInventory().getContents()).toList());
        }
        return result;
    }

    public HashMap<Material, Integer> countItems() {
        HashMap<Material, Integer> map = new HashMap<Material, Integer>();

        for (ItemStack stack : getItems()) {
            if (stack != null) {
                if (map.keySet().contains(stack.getType())) {
                    map.replace(stack.getType(), map.get(stack.getType()) + stack.getAmount());
                } else map.put(stack.getType(), stack.getAmount());
            }
        }

        LinkedHashMap<Material, Integer> sortedMap = new LinkedHashMap<>();
        ArrayList<Integer> list = new ArrayList<>();

        for (Map.Entry<Material, Integer> entry : map.entrySet()) {
            list.add(entry.getValue());
        }
        Collections.sort(list, Comparator.reverseOrder());
        for (int num : list) {
            for (Map.Entry<Material, Integer> entry : map.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sortedMap.put(entry.getKey(), num);
                }
            }
        }
        return sortedMap;
    }
}
