package dev.nanoflux.networks.storage;

import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Network;
import dev.nanoflux.networks.component.NetworkComponent;

import java.util.UUID;

public record SerializableNetwork(

        String version,
        UUID owner,
        UUID[] users,
        NetworkProperties properties,
        NetworkComponent[] components) {

    private static final String VERSION = Main.getPlugin(Main.class).getPluginMeta().getVersion();

    public SerializableNetwork(Network network) {
        this(VERSION, network.owner(), network.users().toArray(new UUID[0]), network.properties(), network.components().toArray(new NetworkComponent[0]));
    }
}
