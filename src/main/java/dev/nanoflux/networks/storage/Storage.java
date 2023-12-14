package dev.nanoflux.networks.storage;

import com.google.gson.*;
import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Network;
import dev.nanoflux.networks.component.NetworkComponent;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Storage implements dev.nanoflux.networks.api.Storage {

    private final Main plugin;
    private final Path path;

    private final Gson gson;

    public Storage(Main plugin) {
        this.plugin = plugin;
        plugin.getDataFolder().mkdirs();
        path = plugin.getDataFolder().toPath().resolve("networks");

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(NetworkComponent.class, new ComponentSerializer());
        if (plugin.getConfiguration().humanReadableJson()) {
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
    public void create(String id, UUID owner) {
        saveNetwork(id, new Network(id, owner));
    }

    /**
     * @param id ID of the Network to delete
     */
    @Override
    public void delete(String id) {
        try {
            if (plugin.getConfiguration().archiveNetworksOnDelete()) {
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
            System.out.println(json);
            System.out.println(new Network(id, gson.fromJson(json, SerializableNetwork.class)));
            return new Network(id, gson.fromJson(json, SerializableNetwork.class));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load Network with ID " + id + ": " + e);
        }
    }


    /**
     * @param id
     * @param newName
     * @return
     */
    @Override
    public void renameNetwork(String id, String newName) {
        try {
            Files.move(path.resolve(id+".json"), path.resolve(newName+".json"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @return
     */
    @Override
    public Set<String> getNetworkIDs() {
        try {
            Set<String> set = new HashSet<>();
            Files.list(path).forEach(file -> set.add(file.getFileName().toString().replace(".json", "")));
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