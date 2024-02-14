package dev.nanoflux.networks.api;

import dev.nanoflux.networks.Network;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.utils.BlockLocation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface Manager {

    boolean create(String id, UUID owner);

    Network getFromName(String id);

    NetworkComponent getComponent(BlockLocation location);
    Network getNetworkWithComponent(BlockLocation location);

    boolean delete(String id);

    boolean rename(String id, String newid);

    Collection<Network> getNetworks();

    Set<String> getNetworkIDs();


    void loadData();

    void saveData();

    List<Network> withUser(UUID uniqueId);

    List<Network> withOwner(UUID uniqueId);

    @Nullable Network selection(CommandSender sender);
    void select(CommandSender sender, Network network);

    boolean permissionOwner(CommandSender sender, Network network);
    boolean permissionUser(CommandSender sender, Network network);


    boolean force(Player player);
    boolean forceToggle(Player player);
}
