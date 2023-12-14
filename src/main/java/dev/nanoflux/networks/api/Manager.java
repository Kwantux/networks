package dev.nanoflux.networks.api;

import dev.nanoflux.networks.Network;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.utils.Location;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public interface Manager {

    boolean create(String id, UUID owner);

    Network getFromName(String id);

    NetworkComponent getComponent(Location location);
    Network getNetworkWithComponent(Location location);

    boolean delete(String id);

    boolean rename(String id, String newid);

    ArrayList<Network> getNetworks();

    Set<String> getNetworkIDs();


    void loadData();

    void saveData();
}
