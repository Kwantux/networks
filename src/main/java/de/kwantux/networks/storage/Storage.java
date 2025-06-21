package de.kwantux.networks.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.kwantux.networks.Main;
import de.kwantux.networks.Network;
import de.kwantux.networks.compat.LegacyNetwork;
import de.kwantux.networks.component.NetworkComponent;
import de.kwantux.networks.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static de.kwantux.networks.Main.logger;

public class Storage implements de.kwantux.networks.api.Storage {

    private static final Logger log = LoggerFactory.getLogger(Storage.class);
    private final Main plugin;
    private final Path path;

    private final Gson gson;

    public Storage(Main plugin) {
        this.plugin = plugin;
        plugin.getDataFolder().mkdirs();
        path = plugin.getDataFolder().toPath().resolve("networks");

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(NetworkComponent.class, new ComponentSerializer());
        if (Config.humanReadableJson) {
            builder.setPrettyPrinting();
        }
        gson = builder.create();
    }


    /**
     * @param id
     * @param owner
     * @return
     */
    @Override
    public boolean create(String id, UUID owner) {
        if (!Network.validName(id)) return false; // Illegal characters
        if (path.resolve(id+".json").toFile().exists()) return false;
        saveNetwork(id, new Network(id, owner));
        return true;
    }

    /**
     * @param id ID of the Network to delete
     */
    @Override
    public void delete(String id) {
        try {
            if (Config.archiveNetworksOnDelete) {
                Files.createDirectories(path.resolve("archive"));
                Files.move(path.resolve(id+".json"), path.resolve("archive/"+id+"-"+ LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)+".json"), StandardCopyOption.REPLACE_EXISTING);
            }
            else Files.delete(path.resolve(id + ".json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Network loadNetwork(String id) {
        try {
            String json = Files.readString(path.resolve(id+".json"), StandardCharsets.UTF_8);
            try {
                // Try modern format
                return new Network(id, gson.fromJson(json, SerializableNetwork.class));
            } catch (RuntimeException e) {
                try {
                    // Try compat format
                    Network network = gson.fromJson(json, LegacyNetwork.class).convert(id);
                    saveNetwork(id, network);
                    return network;
                } catch (RuntimeException ignored) {
                    logger.severe("Unable to load Network with ID " + id + "\n" + "The network file is likely corrupted.");
                    throw e;
                }
            }
        } catch (IOException e) {
            logger.severe("Unable to load Network with ID " + id + "\n" + "This is likely due to incorrectly set file permissions.");
            throw new RuntimeException(e);
        }
    }


    /**
     * @param id
     * @param newName
     * @return
     */
    @Override
    public boolean renameNetwork(String id, String newName) {
        if (path.resolve(newName+".json").toFile().exists()) return false;
        try {
            Files.move(path.resolve(id+".json"), path.resolve(newName+".json"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }


    /**
     * @return
     */
    @Override
    public Set<String> getNetworkIDs() {
        try {
            Set<String> set = new HashSet<>();
            Files.list(path).forEach(file -> {
                if (file.toString().endsWith(".json")) set.add(file.getFileName().toString().replace(".json", ""));
            });
            return set;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @param network
     */
    @Override
    public void saveNetwork(String id, Network network) {
        SerializableNetwork serializable = new SerializableNetwork(network);
        String json = gson.toJson(serializable);
        try {
            Files.write(path.resolve(id+".json"), json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}