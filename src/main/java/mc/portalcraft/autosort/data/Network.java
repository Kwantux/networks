package mc.portalcraft.autosort.data;

import mc.portalcraft.autosort.Autosort;
import mc.portalcraft.autosort.NetworkManager;
import mc.portalcraft.autosort.StorageNetwork;
import mc.portalcraft.autosort.container.*;

import java.util.UUID;


public class Network {
    private String id;
    private String owner;

    private InputContainer[] input;
    private ItemContainer[] sorting;
    private MiscContainer[] misc;

    public Network(StorageNetwork n) {
        this.id = n.getID();
        this.owner = n.getOwner().toString();

        this.input = n.getInputChests().toArray(new InputContainer[n.getInputChests().size()]);
        this.sorting = n.getSortingChests().toArray(new ItemContainer[n.getSortingChests().size()]);
        this.misc = n.getMiscChests().toArray(new MiscContainer[n.getMiscChests().size()]);
    }

}
