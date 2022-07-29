package mc.portalcraft.autosort;


import com.google.gson.Gson;
import mc.portalcraft.autosort.data.Network;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public final class NetworkManager implements Serializable {

    private final ArrayList<StorageNetwork> networks = new ArrayList<StorageNetwork>();


    private final Gson gson = new Gson();


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

        File file = new File("plugins/Autosort/", "networks.json");
        /*if (!file.exists()) {
            file.createNewFile();
        }*/
        /*file.setWritable(true);
        FileWriter filewriter = new FileWriter(file);
        filewriter.write(gson.toJson(networks));
        filewriter.close();*/

        Bukkit.getLogger().info(gson.toJson(list));
    }
    public void loadData() {
        //File opening
        Network[] netarray = {};
        for (Network net: netarray) {
            networks.add(new StorageNetwork(net));
        }
    }
}
