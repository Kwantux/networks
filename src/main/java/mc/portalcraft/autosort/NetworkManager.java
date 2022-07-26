package mc.portalcraft.autosort;


import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NetworkManager implements Serializable {

    ArrayList<StorageNetwork> networks = new ArrayList<StorageNetwork>();


    private Gson gson = new Gson();


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
        Bukkit.getLogger().info("Started saving networks");
        //try {
        Bukkit.getLogger().info(this.networks.toString());
        Bukkit.getLogger().info(listAll().toString());
        Bukkit.getLogger().info("Network size: "+this.networks.size());
        for (int i = 0; i < this.networks.size()-1; i++) {
            /*File file = new File("plugins/Autosort/networks/" + networks.get(i).getID().toLowerCase() + ".json");
            if (!file.exists()) {
                file.createNewFile();
            }
            file.setWritable(true);*/
            /*FileWriter filewriter = new FileWriter("plugins/Autosort/networks/" + networks.get(i).getID().toLowerCase() + ".json");
            filewriter.write(gson.toJson(networks.get(i)));
            filewriter.close();*/
            Bukkit.getLogger().info(networks.get(i).getID());
            Bukkit.getLogger().info("[Autosort] " + gson.toJson(this.networks.get(i)));

        }
        /*return true;

        } catch (FileNotFoundException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        } catch (IOException e) {
            Bukkit.getLogger().warning("An IOException occurred whilst saving network file");
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        }*/
        return true;
    }
    public boolean loadData() {
        /*try {
            new File("plugins/Autosort/networks.json").createNewFile();
            //BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream("plugins/Autosort/networks.json")));
            //this.networks = gson.fromJson(in.read(), ArrayList<StorageNetwork>.class);
            //in.close();
            return true;
        } catch (IOException err) {
            err.printStackTrace();
            return false;
        }*/return true;
    }
}
