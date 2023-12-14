package quantum625.networks.data;

import dev.nanoflux.networks.component.component.InputContainer;
import net.quantum625.networks.component.SortingContainer;
import net.quantum625.networks.component.MiscContainer;
import dev.nanoflux.networks.Network;

import java.util.ArrayList;
import java.util.UUID;


public class JSONNetwork {
    private final String id;
    private final String owner;

    private final String[] users;

    private int maxContainers = 20;
    private int maxRange = 40;

    private final InputContainer[] input_containers;
    private final SortingContainer[] sorting_containers;
    private final MiscContainer[] misc_containers;

    public JSONNetwork(Network n) {
        this.id = n.getID();
        this.owner = n.getOwner().toString();

        this.users = new String[n.getUsers().size()];

        for (int i = 0; i < n.getUsers().size(); i++) {
            users[i] = n.getUsers().get(i).toString();
        }

        this.maxContainers = n.getMaxContainers();
        this.maxRange = n.getMaxRange();

        this.input_containers = n.getInputChests().toArray(new InputContainer[0]);
        this.sorting_containers = n.getSortingChests().toArray(new SortingContainer[0]);
        this.misc_containers = n.getMiscChests().toArray(new MiscContainer[0]);
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public ArrayList<UUID> getUsers() {
        ArrayList<UUID> result = new ArrayList<UUID>();
        for (String user : users) {
            result.add(UUID.fromString(user));
        }
        return result;
    }

    public int getMaxContainers() {return maxContainers;}
    public int getMaxRange() {return maxRange;}

    public InputContainer[] getInputContainers() {
        return input_containers;
    }

    public SortingContainer[] getSortingContainers() {
    return sorting_containers;
    }

    public MiscContainer[] getMiscContainers() {
        return misc_containers;
    }
}
