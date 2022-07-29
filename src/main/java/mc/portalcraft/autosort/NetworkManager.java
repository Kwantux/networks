package mc.portalcraft.autosort;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mc.portalcraft.autosort.data.Network;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public final class NetworkManager implements Serializable {

    private final ArrayList<StorageNetwork> networks = new ArrayList<StorageNetwork>();

    private File dataFolder;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();;


    public NetworkManager(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public boolean add(String id, UUID owner) {
        if (this.getFromID(id) == null) {
            networks.add(new StorageNetwork(id, owner));
            Bukkit.getLogger().info(networks.toString());
            return true;
        }
        return false;
    }

    public boolean delete(String id) {
        if (getFromID(id) != null) {
            networks.remove(getFromID(id));
            return true;
        }
        return false;
    }


    public StorageNetwork getFromID(String id) {
        for (int i = 0; i < networks.size(); i++) {
            if (networks.get(i).getID().equalsIgnoreCase(id)) {
                return networks.get(i);
            }
        }
        return null;
    }

    public ArrayList<StorageNetwork> listAll() {
        return this.networks;
    }

    public ArrayList<StorageNetwork> listFromOwner(UUID owner) {
        ArrayList<StorageNetwork> result = new ArrayList<StorageNetwork>();
        for (StorageNetwork network : networks) {
            if (network.getOwner().equals(owner)) {
                result.add(network);
            }
        }
        return result;
    }


    public void saveData() {
        Bukkit.getLogger().info("Started saving networks");

        Bukkit.getLogger().info("Network size: "+networks.size());

        Bukkit.getLogger().info(gson.toJson(new Network(new StorageNetwork("hi",UUID.randomUUID()))));

        Network[] list = new Network[networks.size()];

        for (int i = 0; i < networks.size(); i++) {
            list[i] = new Network(networks.get(i));
            //Bukkit.getLogger().info(gson.toJson(new Network(networks.get(i))));
        }

        File file = new File(dataFolder, "networks.json");


        // NOT WORKING PART - NEEDS TO BE FIXED:
        /*if (!file.exists()) {
            file.createNewFile();
        }
        file.setWritable(true);
        if (file.canWrite()) {
            FileWriter filewriter = new FileWriter(file);
            filewriter.write(gson.toJson(nfetworks));
            filewriter.close();
        }*/

        Bukkit.getLogger().info(gson.toJson(list));
    }
    public void loadData() {
        /*File file = new File(dataFolder, "networks.json");
        Scanner scanner = new Scanner(file);
        for (Network net: gson.fromJson(scanner.toString(), Network[].class)) {
            networks.add(new StorageNetwork(net));
        }*/
    }
}
