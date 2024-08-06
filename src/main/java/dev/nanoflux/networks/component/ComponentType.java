package dev.nanoflux.networks.component;

import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.component.component.InputContainer;
import dev.nanoflux.networks.component.component.MiscContainer;
import dev.nanoflux.networks.component.component.SortingContainer;
import dev.nanoflux.networks.utils.BlockLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ComponentType {
    public final Class<? extends NetworkComponent> clazz;
    public final String tag;
    public final Component name;

    public final boolean donator;
    public final boolean acceptor;
    public final boolean supplier;
    public final boolean requestor;

    public final BiFunction<BlockLocation, PersistentDataContainer, ?extends NetworkComponent> constructor;
    public final Function<Material, ItemStack> item;

    public static HashMap<String, ComponentType> tags = new HashMap<>();
    public static HashMap<Class<? extends NetworkComponent>, ComponentType> types = new HashMap<>();


    // Component Type Registration
    // Necessary, so that the component types can be registered
    public static ComponentType INPUT = InputContainer.register();
    public static ComponentType SORTING = SortingContainer.register();
    public static ComponentType MISC = MiscContainer.register();


    public static ComponentType register(Class<? extends NetworkComponent> clazz, String tag, Component name, boolean donator, boolean acceptor, boolean supplier, boolean requestor, BiFunction<BlockLocation, PersistentDataContainer, ?extends NetworkComponent> constructor, Function<Material, ItemStack> item) {
        ComponentType type = new ComponentType(clazz, tag, name, donator, acceptor, supplier, requestor, constructor, item);
        tags.put(tag, type);
        types.put(clazz, type);
        return type;
    }

    public static @Nullable ComponentType get(String tag) {
        if (tag == null) return null;
        if (!tags.containsKey(tag)) {
            Main.logger.severe("Component type " + tag + " not found!");
            return null;
        }
        return tags.get(tag);
    }

    public static ComponentType get(Class<? extends NetworkComponent> clazz) {
        return types.get(clazz);
    }

    private ComponentType(Class<? extends NetworkComponent> clazz, String tag, Component name, boolean donator, boolean acceptor, boolean supplier, boolean requestor, BiFunction<BlockLocation, PersistentDataContainer, ?extends NetworkComponent> constructor, Function<Material, ItemStack> item) {

        this.constructor = constructor;
        this.item = item;

        this.clazz = clazz;

        this.tag = tag;
        this.name = name;

        this.donator = donator;
        this.acceptor = acceptor;
        this.supplier = supplier;
        this.requestor = requestor;

    }

    public Class<? extends NetworkComponent> componentClass() {
        return clazz;
    }

    public String tag() {
        return tag;
    }


    public NetworkComponent create(BlockLocation pos, PersistentDataContainer container) {
        return constructor.apply(pos, container);
    }

    public ItemStack item(Material mat) {
        return item.apply(mat);
    }

}
