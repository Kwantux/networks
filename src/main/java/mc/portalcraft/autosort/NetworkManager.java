package mc.portalcraft.autosort;


import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NetworkManager implements Serializable{

    private ArrayList<StorageNetwork> networks = new ArrayList<StorageNetwork>();


    private Gson gson = new Gson();


    public void add(StorageNetwork n) {
        networks.add(n);
        Bukkit.getLogger().info(networks.toString());
        Bukkit.getLogger().info(n.getID().toString());
        Bukkit.getLogger().info(n.getOwner().toString());
    }

    public boolean delete(String id) {
        if (getFromID(id) != null) {
            networks.remove(getFromID(id));
            return true;
        }
        return false;
    }


    public StorageNetwork getFromID(String id) {
        for (int i = 0; i < networks.size() - 1 ; i++) {
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
        for (int i = 0; i < networks.size()-1; i++) {
            if (networks.get(i).getOwner().equals(owner)) {
                result.add(networks.get(i));
            }
        }
        return result;
    }


    public boolean saveData() {
        try {
            new File("plugins/Autosort/networks.json").createNewFile();
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream("plugins/Autosort/networks.json")));
            out.writeChars(gson.toJson(this.networks));
            out.close();
            return true;
        } catch (IOException err) {
            err.printStackTrace();
            return false;
        }
    }
    public boolean loadData() {
        try {
            new File("plugins/Autosort/networks.json").createNewFile();
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream("plugins/Autosort/networks.json")));
            //this.networks = gson.fromJson(in.read(), ArrayList<StorageNetwork>.class);
            in.close();
            return true;
        } catch (IOException err) {
            err.printStackTrace();
            return false;
        }
    }
}
