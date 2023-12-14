package dev.nanoflux.networks;


import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.config.lang.LanguageController;
import dev.nanoflux.networks.storage.Storage;
import dev.nanoflux.networks.utils.Location;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public final class Manager implements dev.nanoflux.networks.api.Manager {
    private final dev.nanoflux.networks.api.Storage storage;

    private HashMap<String, Network> networks = new HashMap<>();
    private HashMap<Location, Network> locations = new HashMap<>();
    private final ArrayList<UUID> noticedPlayers = new ArrayList<>();
    private final Logger logger;



    public Manager(Main plugin) {
        this.storage = new Storage(plugin);
        this.logger = plugin.getLogger();
    }

    public boolean create(String name, UUID owner) {
        if (this.getFromName(name) == null) {
            networks.put(name, new Network(name, owner));
            storage.create(name, owner);
            return true;
        }
        return false;
    }

    public boolean delete(String id) {
        if (getFromName(id) != null) {
            networks.remove(id);
            storage.delete(id);
            return true;
        }
        return false;
    }


    public boolean rename(String name, String newname) {
        Network network = getFromName(name);
        if (network == null) return false;
        if (getFromName(newname) != null) return false;
        network.name(newname);
        storage.renameNetwork(name, newname);
        return true;
    }

    /**
     * @return
     */
    @Override
    public ArrayList<Network> getNetworks() {
        return null;
    }

    /**
     * @return
     */
    @Override
    public Set<String> getNetworkIDs() {
        return networks.keySet();
    }

    /**
     *
     */
    @Override
    public void loadData() {
        networks.clear();
        for (String id : storage.getNetworkIDs()) {
            networks.put(id, storage.loadNetwork(id));
        }
        logger.info("Loaded " + networks.size() + " Networks");
    }

    /**
     *
     */
    @Override
    public void saveData() {
        for (String id : networks.keySet()) {
            storage.saveNetwork(id, networks.get(id));
        }
        logger.info("Saved " + networks.size() + " Networks");
    }


    public Network getFromName(String name) {
        for (Network network : networks.values()) {
            if (network.name().equalsIgnoreCase(name)) {
                return network;
            }
        }
        return null;
    }

    public boolean addComponent(String network, NetworkComponent component) {
        if (locations.containsKey(component.pos())) {
            return false;
        }
        networks.get(network).addComponent(component);
        locations.put(component.pos(), networks.get(network));
        return true;
    }

    /**
     * @param location The location of the requested component
     * @return The component at that location, null if there is no component at that location
     */
    @Override
    public NetworkComponent getComponent(Location location) {
        return null;
    }

    /**
     * @param location
     * @return
     */
    @Override
    public Network getNetworkWithComponent(Location location) {
        return null;
    }

}
