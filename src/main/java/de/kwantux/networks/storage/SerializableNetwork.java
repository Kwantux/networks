package de.kwantux.networks.storage;

import de.kwantux.networks.Network;
import de.kwantux.networks.Main;
import de.kwantux.networks.component.NetworkComponent;

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
