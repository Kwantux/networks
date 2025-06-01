package de.kwantux.networks;


import de.kwantux.networks.component.BasicComponent;
import de.kwantux.networks.component.util.ComponentType;
import de.kwantux.networks.component.util.FilterTranslator;
import de.kwantux.networks.storage.Storage;
import de.kwantux.networks.utils.DoubleChestUtils;
import de.kwantux.networks.utils.Origin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public final class Manager {
    private final Storage storage;
    private final DoubleChestUtils dcu;

    private HashMap<String, Network> networks = new HashMap<>();
    private HashMap<Origin, Network> origins = new HashMap<>();

    private HashMap<CommandSender, Network> selections = new HashMap<>();
    private final ArrayList<UUID> forceMode = new ArrayList<>();
    private final Logger logger;



    public Manager(Main plugin) {
        this.storage = new Storage(plugin);
        this.logger = plugin.getLogger();
        this.dcu = new DoubleChestUtils(this);
    }

    public boolean create(String name, UUID owner) {
        if (this.networks.get(name) == null) {
            networks.put(name, new Network(name, owner));
            storage.create(name, owner);
            return true;
        }
        return false;
    }

    public boolean delete(String id) {
        if (networks.get(id) != null) {
            transferRequests.remove(networks.get(id));
            origins.values().removeIf(network -> network.name().equals(id));
            selections.values().removeIf(network -> network.name().equals(id));
            networks.remove(id);
            storage.delete(id);
            return true;
        }
        return false;
    }


    public boolean rename(String name, String newname) {
        Network network = networks.get(name);
        if (network == null) return false;
        if (networks.get(newname) != null) return false;
        network.name(newname);
        storage.renameNetwork(name, newname);
        return true;
    }

    /**
     * @return
     */
    public Collection<Network> getNetworks() {
        return networks.values();
    }

    /**
     * @return
     */
    public Set<String> getNetworkIDs() {
        return networks.keySet();
    }

    /**
     *
     */
    public void loadData() {
        networks.clear();
        for (String id : storage.getNetworkIDs()) {
            networks.put(id, storage.loadNetwork(id));
            for (BasicComponent coponent : networks.get(id).components()) {
                origins.put(coponent.origin(), networks.get(id));
            }
        }
        logger.info("Loaded " + networks.size() + " Networks");
    }

    /**
     *
     */
    public void saveData() {
        for (String id : networks.keySet()) {
            storage.saveNetwork(id, networks.get(id));
        }
        try {
            FilterTranslator.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Network getFromName(String name) {
        return networks.get(name);
    }


    public void createComponent(Network network, ComponentType type, Origin origin, PersistentDataContainer container) {
        BasicComponent component = type.create(origin, container);
        addComponent(network, component);
    }

    public boolean addComponent(Network network, BasicComponent component) {
        if (component == null) return false;
        if (origins.containsKey(component.origin())) {
            return false;
        }
        network.addComponent(component);
        origins.put(component.origin(), network);
        dcu.checkChest(component.origin());
        return true;
    }

    public void removeComponent(Network network, BasicComponent component) {
        if (!origins.containsKey(component.origin())) {
            throw new IllegalArgumentException("Component " + component + " does not exist");
        }
        network.removeComponent(component);
        origins.remove(component.origin());
    }

    public void removeComponent(Origin component) {
        if (!origins.containsKey(component)) {
            throw new IllegalArgumentException("Component " + component + " does not exist");
        }
        origins.get(component).removeComponent(component);
        origins.remove(component);
    }


    /**
     * @param location The location of the requested component
     * @return The component at that location, null if there is no component at that location
     */
    public BasicComponent getComponent(@NotNull Origin location) {
        Network network = getNetworkWithComponent(location);
        if (network == null) return null;
        return network.getComponent(location);
    }

    /**
     * @param location
     * @return
     */
    public Network getNetworkWithComponent(@NotNull Origin location) {
        Network network = null;
        for (Origin loc : origins.keySet()) {
            if (location.equals(loc)) {
                network = origins.get(loc);
                break;
            }
        }
        if (network == null) return null;
        return network;
    }

    public List<Network> withUser(UUID user) {
        List<Network> result = new ArrayList<>();
        for (Network net : networks.values()) {
            if (net.users().contains(user) || net.owner().equals(user)) result.add(net);
        }
        return result;
    }

    public List<Network> withOwner(UUID user) {
        List<Network> result = new ArrayList<>();
        for (Network net : networks.values()) {
            if (net.owner().equals(user)) result.add(net);
        }
        return result;
    }


    @Nullable public Network selection(CommandSender sender) {
        return selections.get(sender);
    }

    public void select(CommandSender sender, Network network) {
        selections.put(sender, network);
    }

    public boolean permissionOwner(CommandSender sender, Network network) {
        if (sender instanceof Player player) return network.owner().equals(player.getUniqueId()) || force(player);
        return true;
    }
    public boolean permissionUser(CommandSender sender, Network network) {
        if (sender instanceof Player player) return network.owner().equals(player.getUniqueId()) || network.users().contains(player.getUniqueId()) || force(player);
        return true;
    }

    public boolean force(Player player) {
        return forceMode.contains(player.getUniqueId());
    }

    public boolean forceToggle(Player player) {
        if (force(player)) {
            forceMode.remove(player.getUniqueId());
            return false;
        }
        else {
            forceMode.add(player.getUniqueId());
            return true;
        }
    }


    //
    // Owner Transfer
    //

    Map<Network, Player> transferRequests = new HashMap<>();

    public boolean canTransfer(Network network, Player player) {
        if (transferRequests.containsKey(network)){
            return transferRequests.get(network).equals(player);
        }
        return false;
    }

    public void acceptTransfer(Network network) {
        transferRequests.remove(network);
    }

    public void requestTransfer(Network network, Player player) {
        transferRequests.put(network, player);
    }

}
