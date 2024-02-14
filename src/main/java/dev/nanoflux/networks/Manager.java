package dev.nanoflux.networks;


import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.storage.Storage;
import dev.nanoflux.networks.utils.BlockLocation;
import dev.nanoflux.networks.utils.DoubleChestUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import javax.annotation.Nullable;
import java.util.*;
import java.util.logging.Logger;

public final class Manager implements dev.nanoflux.networks.api.Manager {
    private final dev.nanoflux.networks.api.Storage storage;
    private final DoubleChestUtils dcu;

    private HashMap<String, Network> networks = new HashMap<>();
    private HashMap<BlockLocation, Network> locations = new HashMap<>();

    private HashMap<CommandSender, Network> selections = new HashMap<>();
    private final ArrayList<UUID> noticedPlayers = new ArrayList<>();
    private final ArrayList<UUID> forceMode = new ArrayList<>();
    private final Logger logger;



    public Manager(Main plugin) {
        this.storage = new Storage(plugin);
        this.logger = plugin.getLogger();
        this.dcu = new DoubleChestUtils(this);
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
    public Collection<Network> getNetworks() {
        return networks.values();
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
            for (NetworkComponent coponent : getFromName(id).components()) {
                locations.put(coponent.pos(), getFromName(id));
            }
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


    public void createComponent(Network network, Material material, ComponentType type, BlockLocation pos, PersistentDataContainer container) {
        NetworkComponent component = type.create(pos, container);
        addComponent(network, component);
        pos.getBlock().setType(material);
        dcu.checkChest(pos);
    }

    public boolean addComponent(Network network, NetworkComponent component) {
        if (locations.containsKey(component.pos())) {
            return false;
        }
        network.addComponent(component);
        locations.put(component.pos(), networks.get(network));
        return true;
    }


    /**
     * @param location The location of the requested component
     * @return The component at that location, null if there is no component at that location
     */
    @Override
    public NetworkComponent getComponent(BlockLocation location) {
        Network network = locations.get(location);
        for (BlockLocation loc : locations.keySet()) {
            if (location.equals(loc)) {
                network = locations.get(loc);
                break;
            }
        }
        if (network == null) return null;
        return network.componentAt(location);
    }

    /**
     * @param location
     * @return
     */
    @Override
    public Network getNetworkWithComponent(BlockLocation location) {
        return locations.get(location);
    }

    @Override
    public List<Network> withUser(UUID user) {
        List<Network> result = new ArrayList<>();
        for (Network net : networks.values()) {
            if (net.users().contains(user) || net.owner().equals(user)) result.add(net);
        }
        return result;
    }

    @Override
    public List<Network> withOwner(UUID user) {
        List<Network> result = new ArrayList<>();
        for (Network net : networks.values()) {
            if (net.owner().equals(user)) result.add(net);
        }
        return result;
    }


    @Override
    @Nullable public Network selection(CommandSender sender) {
        return selections.get(sender);
    }

    @Override
    public void select(CommandSender sender, Network network) {
        selections.put(sender, network);
    }

    @Override
    public boolean permissionOwner(CommandSender sender, Network network) {
        if (sender instanceof Player player) return network.owner().equals(player.getUniqueId()) || force(player);
        return true;
    }
    @Override
    public boolean permissionUser(CommandSender sender, Network network) {
        if (sender instanceof Player player) return network.owner().equals(player.getUniqueId()) || network.users().contains(player.getUniqueId()) || force(player);
        return true;
    }


    @Override
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

}
