package de.kwantux.networks.component;

import de.kwantux.networks.Main;
import de.kwantux.networks.component.component.InputContainer;
import de.kwantux.networks.component.component.MiscContainer;
import de.kwantux.networks.component.component.SortingContainer;
import de.kwantux.networks.utils.Origin;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class ComponentType {
    public final Class<? extends BasicComponent> clazz;
    public final String tag;
    public final Component name;

    /**
     * Whether the component type is saved to disk on shutdown
     */
    public final boolean persistent;

    public final boolean donator;
    public final boolean acceptor;
    public final boolean supplier;
    public final boolean requestor;

    public final BiFunction<Origin, PersistentDataContainer, ?extends BasicComponent> constructor;
    public final Map<String, Object> defaultProperties;

    public static HashMap<String, ComponentType> tags = new HashMap<>();
    public static HashMap<Class<? extends BasicComponent>, ComponentType> types = new HashMap<>();


    // Component Type Registration
    // Necessary, so that the component types can be registered
    public static ComponentType INPUT = InputContainer.register();
    public static ComponentType SORTING = SortingContainer.register();
    public static ComponentType MISC = MiscContainer.register();


    public static ComponentType register(Class<? extends BasicComponent> clazz, String tag, Component name, boolean donator, boolean acceptor, boolean supplier, boolean requestor, boolean persistent, BiFunction<Origin, PersistentDataContainer, ?extends BasicComponent> constructor, Map<String, Object> defaultProperties) {
        ComponentType type = new ComponentType(clazz, tag, name, donator, acceptor, supplier, requestor, persistent, constructor, defaultProperties);
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

    public static ComponentType get(Class<? extends BasicComponent> clazz) {
        return types.get(clazz);
    }

    private ComponentType(Class<? extends BasicComponent> clazz, String tag, Component name, boolean donator, boolean acceptor, boolean supplier, boolean requestor, boolean persistent, BiFunction<Origin, PersistentDataContainer, ?extends BasicComponent> constructor, Map<String, Object> defaultProperties) {
        this.constructor = constructor;
        this.defaultProperties = defaultProperties;

        this.clazz = clazz;

        this.tag = tag;
        this.name = name;

        this.donator = donator;
        this.acceptor = acceptor;
        this.supplier = supplier;
        this.requestor = requestor;
        this.persistent = persistent;
    }

    public Class<? extends BasicComponent> componentClass() {
        return clazz;
    }

    public String tag() {
        return tag;
    }


    public @Nullable BasicComponent create(Origin pos, PersistentDataContainer container) {
        return constructor.apply(pos, container);
    }

    public ItemStack item() {
        return BasicComponent.item(this, defaultProperties);
    }

}
