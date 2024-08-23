package dev.nanoflux.networks.utils;

import dev.nanoflux.networks.Main;
import org.bukkit.NamespacedKey;

public enum NamespaceUtils {

    // Component Properties
    COMPONENT,
    RANGE,
    ACCEPTOR_PRIORITY,
    SUPPLIER_PRIORITY,
    FILTERS,

    // Network Properties
    NETWORK,
    BASE_RANGE,
    MAX_USERS,
    MAX_COMPONENTS,

    // Items
    WAND,

    // UI
    BUTTON;

    private static final Main main = Main.getPlugin(Main.class);

    public final String name;
    NamespaceUtils() {
        this.name = this.name().toLowerCase();
    }

    public NamespacedKey key() {
        return new NamespacedKey(main, this.name);
    }

    public static NamespacedKey key(String name) {
        return new NamespacedKey(main, name);
    }
}
