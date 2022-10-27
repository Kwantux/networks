package com.quantum625.networks;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quantum625.networks.commands.LanguageModule;
import com.quantum625.networks.component.InputContainer;
import com.quantum625.networks.data.Config;
import com.quantum625.networks.data.JSONNetwork;
import com.quantum625.networks.utils.Location;
import com.quantum625.networks.utils.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public final class NetworkManager implements Serializable {

    private ArrayList<Network> networks = new ArrayList<Network>();

    private ArrayList<PlayerData> selections = new ArrayList<PlayerData>();

    private ArrayList<UUID> noticedPlayers = new ArrayList<UUID>();

    private Map<Location, Network> input_locations = new HashMap<Location, Network>();
    private Network console_selection = null;
    private Location console_location = null;

    private Config config;
    private File dataFolder;
    private LanguageModule lang;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();;


    public NetworkManager(Config config, File dataFolder, LanguageModule lang) {
        this.config = config;
        this.dataFolder = dataFolder;
        this.lang = lang;
    }

    public boolean add(String id, UUID owner) {
        if (this.getFromID(id) == null) {
            networks.add(new Network(id, owner, config.getBaseContainers(), config.getBaseRange()));
            return true;
        }
        return false;
    }

    public boolean delete(String id) {
        if (getFromID(id) != null) {
            networks.remove(getFromID(id));
            File file = new File(dataFolder, "networks/"+id+".json");
            file.delete();
            return true;
        }
        return false;
    }


    public Network getFromID(String id) {
        for (int i = 0; i < networks.size(); i++) {
            if (networks.get(i).getID().equalsIgnoreCase(id)) {
                return networks.get(i);
            }
        }
        return null;
    }

    public boolean sortContainer(Location pos) {
        if (input_locations.get(pos) != null) {
            input_locations.get(pos).sort(pos);
            return true;
        }
        else {
            for (Network network : listAll()) {
                if (network.getInputContainerByLocation(pos) != null) {
                    network.sort(pos);
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<Network> listAll() {
        return this.networks;
    }

    public ArrayList<Network> listFromOwner(UUID player) {
        ArrayList<Network> result = new ArrayList<Network>();
        for (Network network : networks) {
            if (network.getOwner().equals(player)) {
                result.add(network);
            }
        }
        return result;
    }

    public ArrayList<Network> listFromUser(UUID player) {
        ArrayList<Network> result = new ArrayList<Network>();
        for (Network network : networks) {
            if (network.getUsers().contains(player)) {
                result.add(network);
            }
            if (network.getOwner().equals(player)) {
                result.add(network);
            }
        }
        return result;
    }


    public ArrayList<UUID> getNoticedPlayers() {
        ArrayList<UUID> result = new ArrayList<>(noticedPlayers);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (listFromUser(player.getUniqueId()).size() > 0) {
                result.add(player.getUniqueId());
            }
        }
        return result;
    }

    public void noticePlayer(Player player) {
        if (config.noticeEnabled()) {
            if (!getNoticedPlayers().contains(player.getUniqueId())) {
                lang.returnMessage(player, "notice");
                noticedPlayers.add(player.getUniqueId());
            }
        }
    }


    public int checkNetworkPermission(Player player, Network network) {

        if (player.hasPermission("networks.admin.foreign.owner")) {
            //Bukkit.getLogger().info("[Networks] Player has admin permission for foreign owner");
            return 2; // Server admin permission
        }
        if (player.hasPermission("networks.admin.foreign.user")) {
            //Bukkit.getLogger().info("[Networks] Player has admin permission for foreign user");
            return 1; // Server admin permission
        }

        if (network.getOwner().equals(player.getUniqueId())) {
            //Bukkit.getLogger().info("[Networks] Player is an owner");
            return 2; // Network Owner permission
        }
        if (listFromUser(player.getUniqueId()).contains(network)) {
            //Bukkit.getLogger().info("[Networks] Player is a user");
            return 1; // Network User permission
        }

        return 0; // No permission
    }
    public int checkNetworkRank(Player player, Network network) {

        if (network.getOwner().equals(player)) {
            return 2; // Network Owner permission
        }

        if (listFromUser(player.getUniqueId()).contains(network)) {
            return 1; // Network User permission
        }

        return 0; // No permission
    }



    public void saveData() {
        for (int i = 0; i < networks.size(); i++) {
            JSONNetwork n = new JSONNetwork(networks.get(i));
            File file = new File(dataFolder, "networks/" + n.getId() + ".json");

            try {

                if (!file.exists()) {
                    file.createNewFile();
                }
                file.setWritable(true);
                if (file.canWrite()) {
                    FileWriter filewriter = new FileWriter(file);
                    filewriter.write(gson.toJson(networks.get(i)));
                    filewriter.close();
                }

            }
            catch (IOException e) {
                Bukkit.getLogger().warning("[Networks] Failed to save network file " + n.getId() + ".json");
                e.printStackTrace();
                Bukkit.getLogger().info(e.getStackTrace().toString());
            }
        }



    }

    public void loadData() {
        for (File file : new File(dataFolder, "networks/").listFiles()) {
            try {
                Scanner scanner = new Scanner(file);
                String json = "";
                while (scanner.hasNext()) {
                    json += scanner.next();
                }

                networks.add(new Network(gson.fromJson(json, JSONNetwork.class)));

                //Bukkit.getLogger().info("[Networks] Successfully loaded network " + file.getName());
            } catch (IOException e) {
                Bukkit.getLogger().warning("[Networks] Failed to load network " + file.getName());
                e.printStackTrace();
            }
        }
    }




    public void selectNetwork(Player player, Network network) {
        for (PlayerData networkSelection : selections) {
            if (networkSelection.getPlayer().equals(player)) {
                networkSelection.setNetwork(network);
                break;
            }
        }
        PlayerData pd = new PlayerData(player);
        pd.setNetwork(network);
        selections.add(pd);
    }

    public Network getSelectedNetwork(Player player) {
        for (PlayerData networkSelection : selections) {
            if (networkSelection.getPlayer().equals(player)) {
                return networkSelection.getNetwork();
            }
        }
        return null;
    }



    public void selectLocation(Player player, Location location) {
        for (PlayerData LocationSelection : selections) {
            if (LocationSelection.getPlayer().equals(player)) {
                LocationSelection.setLocation(location);
                break;
            }
        }
        PlayerData pd = new PlayerData(player);
        pd.setLocation(location);
        selections.add(pd);
    }

    public Location getSelectedLocation(Player player) {
        for (PlayerData locationSelection : selections) {
            if (locationSelection.getPlayer().equals(player)) {
                return locationSelection.getLocation();
            }
        }
        return null;
    }



    public void selectComponentType(Player player, @Nullable String component) {
        for (PlayerData componentSelection : selections) {
            if (componentSelection.getPlayer().equals(player)) {
                componentSelection.setComponentType(component);
                break;
            }
        }
        PlayerData pd = new PlayerData(player);
        pd.setComponentType(component);
        selections.add(pd);
    }

    public String getSelectedComponentType(Player player) {
        for (PlayerData componentSelection : selections) {
            if (componentSelection.getPlayer().equals(player)) {
                return componentSelection.getComponentType();
            }
        }
        return null;
    }


    public void selectItems(Player player, String[] items) {
        for (PlayerData itemSelection : selections) {
            if (itemSelection.getPlayer().equals(player)) {
                for (String item : items) {
                    item = item.toUpperCase();
                }
                itemSelection.setItems(items);
                break;
            }
        }
        PlayerData pd = new PlayerData(player);
        pd.setItems(items);
        selections.add(pd);
    }

    public String[] getSelectedItems(Player player) {
        for (PlayerData itemSelection : selections) {
            if (itemSelection.getPlayer().equals(player)) {
                return itemSelection.getItems();
            }
        }
        return null;
    }

    public void consoleSelectNetwork(Network network) {
        console_selection = network;
    }

    public Network getConsoleSelection() {
        return console_selection;
    }

    public void consoleSelectLocation(Location location) {console_location = location;}

    public Location getConsoleLocation() {return console_location;}
}
