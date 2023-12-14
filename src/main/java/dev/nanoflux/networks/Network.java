package dev.nanoflux.networks;

import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.component.module.Acceptor;
import dev.nanoflux.networks.component.module.Donator;
import dev.nanoflux.networks.component.module.Supplier;
import dev.nanoflux.networks.storage.NetworkProperties;
import dev.nanoflux.networks.storage.SerializableNetwork;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Network {
    private String id;

    private UUID owner;
    private final ArrayList<UUID> users = new ArrayList<>();
    private final ArrayList<NetworkComponent> components = new ArrayList<>();

    // Network Properties

    private int range;
    private int maxUsers;
    private int maxComponents;


    // Constructors

    public Network(String name, UUID owner) {
        this.id = name;
        this.owner = owner;
        properties(Main.config.defaultProperties());
    }

    public Network(String id, SerializableNetwork network) {
        this.id = id;
        this.owner = network.owner();
        properties(network.properties());
    }


    public String name() {
        return this.id;
    }
    public void name(String newName) {
        id = newName;
    }

    public UUID owner() {
        return this.owner;
    }

    public void owner(UUID owner) {
        this.owner = owner;
    }

    public ArrayList<UUID> users() {return users;}

    public void addUser(UUID player) {
        users.add(player);
    }
    public void removeUser(UUID player) {
        users.remove(player);
    }

    public List<? extends NetworkComponent> components() {
        return components;
    }

    public List<? extends Supplier> suppliers() {
        ArrayList<Supplier> suppliers = new ArrayList<>();
        for (NetworkComponent component : components) {
            if (component instanceof Supplier) {
                suppliers.add((Supplier) component);
            }
        }
        return suppliers.stream().sorted(Comparator.comparingInt(Supplier::supplierPriority)).toList();
    }

    public List<? extends Acceptor> acceptors() {
        ArrayList<Acceptor> acceptors = new ArrayList<>();
        for (NetworkComponent component : components) {
            if (component instanceof Acceptor) {
                acceptors.add((Acceptor) component);
            }
        }
        return acceptors.stream().sorted(Comparator.comparingInt(Acceptor::acceptorPriority)).toList();
    }


    public void addComponent(NetworkComponent component) {
        components.add(component);
    }

    public NetworkProperties properties() {
        return new NetworkProperties(range, maxComponents, maxUsers);
    }

    public void properties(@NotNull NetworkProperties properties) {
        this.range = properties.baseRange();
        this.maxUsers = properties.maxUsers();
        this.maxComponents = properties.maxComponents();
    }
}
