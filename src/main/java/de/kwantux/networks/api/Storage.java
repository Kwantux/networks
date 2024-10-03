package de.kwantux.networks.api;

import de.kwantux.networks.Network;

import java.util.*;

public interface Storage {

    boolean create(String id, UUID owner);
    void delete(String id);
    boolean renameNetwork(String id, String newName);

    Set<String> getNetworkIDs();

    Network loadNetwork(String id);
    void saveNetwork(String id, Network network);

}
