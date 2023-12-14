package dev.nanoflux.networks.component;

import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.component.component.InputContainer;
import dev.nanoflux.networks.component.component.MiscContainer;
import dev.nanoflux.networks.component.component.SortingContainer;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class ComponentType {
    public final Class<? extends NetworkComponent> clazz;
    public final String tag;
    public final Component name;

    public final boolean donator;
    public final boolean acceptor;
    public final boolean supplier;
    public final boolean requestor;

    public static HashMap<String, ComponentType> tags = new HashMap<>();
    public static HashMap<Class<? extends NetworkComponent>, ComponentType> types = new HashMap<>();



    static {
        InputContainer.register();
        SortingContainer.register();
        MiscContainer.register();
    }

    public static ComponentType INPUT = InputContainer.type;
    public static ComponentType SORTING = SortingContainer.type;
    public static ComponentType MISC = MiscContainer.type;


    public static ComponentType register(Class<? extends NetworkComponent> clazz, String tag, Component name, boolean donator, boolean acceptor, boolean supplier, boolean requestor) {
        ComponentType type = new ComponentType(clazz, tag, name, donator, acceptor, supplier, requestor);
        tags.put(tag, type);
        types.put(clazz, type);
        return type;
    }

    public static ComponentType get(String tag) {
        if (!tags.containsKey(tag)) {
            Main.logger.severe("Component type " + tag + " not found!");
            return null;
        }
        return tags.get(tag);
    }

    public static ComponentType get(Class<? extends NetworkComponent> clazz) {
        return types.get(clazz);
    }

    private ComponentType(Class<? extends NetworkComponent> clazz, String tag, Component name, boolean donator, boolean acceptor, boolean supplier, boolean requestor) {

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
}
