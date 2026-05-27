package de.kwantux.networks.utils;

import de.kwantux.networks.Main;
import org.bukkit.NamespacedKey;

public enum NamespaceUtils {

    // Component Properties
    NETWORK,
    COMPONENT,
    RANGE,
    ACCEPTOR_PRIORITY,
    SUPPLIER_PRIORITY,
    FILTERS,

    // Item Properties
    WAND,
    UPGRADE_RANGE("upgrade.range");

    public final String name;
    public final NamespacedKey key;

    NamespaceUtils() {
        this.name = this.name().toLowerCase();
        key = new NamespacedKey(Main.instance, this.name);
    }

    NamespaceUtils(String name) {
        this.name = name;
        key = new NamespacedKey(Main.instance, this.name);
    }

    public static NamespacedKey key(String name) {
        return new NamespacedKey(Main.instance, name);
    }

}
