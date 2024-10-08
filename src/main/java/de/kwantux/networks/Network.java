package de.kwantux.networks;

import de.kwantux.networks.component.module.Acceptor;
import de.kwantux.networks.component.module.Supplier;
import de.kwantux.networks.component.NetworkComponent;
import de.kwantux.networks.storage.NetworkProperties;
import de.kwantux.networks.storage.SerializableNetwork;
import de.kwantux.networks.utils.BlockLocation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Network {
    private String id;

    private UUID owner;
    private List<UUID> users = new ArrayList<>();
    private List<NetworkComponent> components = new ArrayList<>();

    // Network Properties

    private int range;
    private int maxUsers;
    private int maxComponents;


    // Constructors

    public Network(String name, UUID owner) {
        this.id = name;
        this.owner = owner;
        properties(Main.cfg.defaultProperties());
    }

    public Network(String id, SerializableNetwork network) {
        this.id = id;
        this.owner = network.owner();
        properties(network.properties());
        users = new ArrayList<>(Arrays.stream(network.users()).toList());
        components = new ArrayList<>(Arrays.asList(network.components()));
        components.removeAll(Collections.singleton(null));
    }


    static public boolean validName(String name) {
        return name.matches("^[a-zA-Z0-9_-]*$") && name.length() <= 20 && name.length() >= 3;
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

    public List<UUID> users() {return users;}

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
        return suppliers.stream().sorted(Comparator.comparingInt(Supplier::supplierPriority).reversed()).toList();
    }

    public List<? extends Acceptor> acceptors() {
        ArrayList<Acceptor> acceptors = new ArrayList<>();
        for (NetworkComponent component : components) {
            if (component instanceof Acceptor) {
                acceptors.add((Acceptor) component);
            }
        }
        return acceptors.stream().sorted(Comparator.comparingInt(Acceptor::acceptorPriority).reversed()).toList();
    }

    public NetworkComponent getComponent(BlockLocation location) {
        for (NetworkComponent component : components) {
            if (component.pos().equals(location)) {
                return component;
            }
        }
        return null;
    }


    /**
     * Add a component to the network
     * ONLY FOR INTERNAL USAGE
     * Use {@link Manager#addComponent(Network, NetworkComponent)} instead
     */
    public void addComponent(NetworkComponent component) {
        components.add(component);
    }
    /**
     * Add a component to the network
     * ONLY FOR INTERNAL USAGE
     * Use {@link Manager#removeComponent(Network, NetworkComponent)} instead
     */
    public void removeComponent(NetworkComponent component) {
        components.remove(component);
    }
    /**
     * Add a component to the network
     * ONLY FOR INTERNAL USAGE
     * Use {@link Manager#removeComponent(Network, NetworkComponent)} instead
     */
    public void removeComponent(BlockLocation blockLocation) {
        components.remove(getComponent(blockLocation));
    }

    public NetworkProperties properties() {
        return new NetworkProperties(range, maxComponents, maxUsers);
    }

    public void properties(@NotNull NetworkProperties properties) {
        this.range = properties.baseRange();
        this.maxUsers = properties.maxUsers();
        this.maxComponents = properties.maxComponents();
    }

    public Component displayText() {
        Component userlist = Component.text(Objects.requireNonNullElse(Bukkit.getOfflinePlayer(owner).getName(), owner.toString())).decorate(TextDecoration.UNDERLINED).decorate(TextDecoration.BOLD);
        userlist = userlist.append(Component.newline().decoration(TextDecoration.BOLD, false).decoration(TextDecoration.UNDERLINED, false));
        for (UUID user : users) {
            userlist = userlist.append(Component.text(Objects.requireNonNullElse(Bukkit.getOfflinePlayer(user).getName(), user.toString())).decorate(TextDecoration.UNDERLINED).decorate(TextDecoration.BOLD));
            userlist = userlist.append(Component.newline());
        }
        return Component.text(name()).hoverEvent(HoverEvent.showText(userlist));
    }

    public ArrayList<ItemStack> items() {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (NetworkComponent component : components) {
            Inventory inventory = component.inventory();
            if (inventory == null) continue;
            stacks.addAll(Arrays.stream(inventory.getContents()).toList());
        }
        return stacks;
    }

    public HashMap<Material, Integer> materials() {
        HashMap<Material, Integer> materials = new HashMap<>();
        for (NetworkComponent component : components) {
            Inventory inventory = component.inventory();
            if (inventory == null) continue;
            for (ItemStack stack : inventory.getContents()) {
                if (stack == null) continue;
                int existing = Objects.requireNonNullElse(materials.get(stack.getType()), 0);
                materials.put(stack.getType(), existing + stack.getAmount());
            }
        }
        return materials;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Network network) return Objects.equals(network.id, id);
        return false;
    }


    public int range() {
        return range;
    }
}
