package com.quantum625.autosort.data;

import com.quantum625.autosort.container.InputContainer;
import com.quantum625.autosort.container.ItemContainer;
import com.quantum625.autosort.container.MiscContainer;
import com.quantum625.autosort.StorageNetwork;
import com.quantum625.autosort.container.*;


public class Network {
    private String id;
    private String owner;

    private InputContainer[] input_containers;
    private ItemContainer[] sorting_containers;
    private MiscContainer[] misc_containers;

    public Network(StorageNetwork n) {
        this.id = n.getID();
        this.owner = n.getOwner().toString();

        this.input_containers = n.getInputChests().toArray(new InputContainer[n.getInputChests().size()]);
        this.sorting_containers = n.getSortingChests().toArray(new ItemContainer[n.getSortingChests().size()]);
        this.misc_containers = n.getMiscChests().toArray(new MiscContainer[n.getMiscChests().size()]);
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public InputContainer[] getInputContainers() {
        return input_containers;
    }

    public ItemContainer[] getSortingContainers() {
    return sorting_containers;
    }

    public MiscContainer[] getMiscContainers() {
        return misc_containers;
    }
}
