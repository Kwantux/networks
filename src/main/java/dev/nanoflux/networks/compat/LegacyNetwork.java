package dev.nanoflux.networks.compat;

import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Network;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.component.component.InputContainer;
import dev.nanoflux.networks.component.component.MiscContainer;
import dev.nanoflux.networks.component.component.SortingContainer;
import dev.nanoflux.networks.storage.NetworkProperties;
import dev.nanoflux.networks.storage.SerializableNetwork;
import dev.nanoflux.networks.utils.BlockLocation;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static dev.nanoflux.networks.Main.cfg;
import static dev.nanoflux.networks.Main.logger;

public record LegacyNetwork(
    UUID owner,
    UUID[] users,
    int maxRange,
    LegacyInputContainer[] input_containers,
    LegacySortingContainer[] sorting_containers,
    LegacyMiscContainer[] misc_containers
) {


    public Network convert(String id) {

        logger.info("Converting compat network '" + id + "'");

        NetworkProperties properties = cfg.defaultProperties();
        properties.baseRange(maxRange);

        List<NetworkComponent> components = new ArrayList<>();
        Arrays.stream(input_containers).toList().forEach(input_container -> components.add(input_container.convert()));
        Arrays.stream(sorting_containers).toList().forEach(sorting_container -> components.add(sorting_container.convert()));
        Arrays.stream(misc_containers).toList().forEach(misc_container -> components.add(misc_container.convert()));
        components.removeIf(Objects::isNull);


        return new Network(
            id,
            new SerializableNetwork(
                Main.getPlugin(Main.class).getPluginMeta().getVersion(),
                owner,
                users,
                properties,
                components.toArray(new NetworkComponent[0])
            )
        );
    }

    record LegacyLocation(
        int x,
        int y,
        int z,
        String dim
    ) {
      public @Nullable BlockLocation convert() {
          try {
              return new BlockLocation(x, y, z, Objects.requireNonNull(uidMap.get(dim)));
          } catch (NullPointerException _e) {
              Main.logger.severe("Unable to upgrade corrupted component at [" + x + " " + y + " " + z + " " + dim + "]\nIt will be permanently removed from the network.");
              return null;
          }
      }
    }

    record LegacyInputContainer(
            LegacyLocation pos
    ) {
        public @Nullable InputContainer convert() {
            if (pos == null) return null;
            return new InputContainer(pos.convert());
        }
    }

    record LegacySortingContainer(
            LegacyLocation pos,
            String[] filters,
            int priority
    ) {
        public @Nullable SortingContainer convert() {
            if (pos == null) return null;
            return new SortingContainer(pos.convert(), filters, priority);
        }
    }

    record LegacyMiscContainer(
            LegacyLocation pos,
            int priority
    ) {
        public @Nullable MiscContainer convert() {
            if (pos == null) return null;
            return new MiscContainer(pos.convert(), priority);
        }
    }

    static Map<String, UUID> uidMap = new java.util.HashMap<>();

    static {
        try {
            for (Path path : Files.list(Bukkit.getWorldContainer().toPath()).toList()) {
                if (Files.isDirectory(path)) {
                    Path file = path.resolve("uid.dat");
                    if (Files.exists(file)) {
                        uidMap.put(path.getFileName().toString(), UUID.nameUUIDFromBytes(Files.readAllBytes(file)));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
