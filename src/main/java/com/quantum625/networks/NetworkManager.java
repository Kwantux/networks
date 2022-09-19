package com.quantum625.networks;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quantum625.networks.data.JSONNetwork;
import com.quantum625.networks.utils.Location;
import com.quantum625.networks.utils.PlayerData;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Scanner;
import java.util.UUID;

public final class NetworkManager implements Serializable {

    private final ArrayList<Network> networks = new ArrayList<Network>();

    private ArrayList<PlayerData> selections = new ArrayList<PlayerData>();
    private Network console_selection = null;
    private Location console_location = null;

    private File dataFolder;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();;


    public NetworkManager(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public boolean add(String id, UUID owner) {
        if (this.getFromID(id) == null) {
            networks.add(new Network(id, owner));
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


    public Network getFromID(String id) {
        for (int i = 0; i < networks.size(); i++) {
            if (networks.get(i).getID().equalsIgnoreCase(id)) {
                return networks.get(i);
            }
        }
        return null;
    }

    public ArrayList<Network> listAll() {
        return this.networks;
    }

    public ArrayList<Network> listFromOwner(UUID owner) {
        ArrayList<Network> result = new ArrayList<Network>();
        for (Network network : networks) {
            if (network.getOwner().equals(owner)) {
                result.add(network);
            }
        }
        return result;
    }



    public void saveData() {
        Bukkit.getLogger().info("Started saving networks");

        Bukkit.getLogger().info("JSONNetwork size: "+networks.size());

        Bukkit.getLogger().info(gson.toJson(new JSONNetwork(new Network("hi",UUID.randomUUID()))));

        JSONNetwork[] list = new JSONNetwork[networks.size()];

        for (int i = 0; i < networks.size(); i++) {
            list[i] = new JSONNetwork(networks.get(i));
            File file = new File(dataFolder, "networks/" + list[i].getId() + ".json");

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

                Bukkit.getLogger().info("Successfully written to file " + list[i].getId() + ".json!");

            }
            catch (IOException e) {
                Bukkit.getLogger().warning("[Main] Failed to save network file " + list[i].getId() + ".json");
                e.printStackTrace();
                Bukkit.getLogger().info(e.getStackTrace().toString());
            }
            Bukkit.getLogger().info(gson.toJson(list));
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

                Bukkit.getLogger().info(json);
            } catch (IOException e) {
                Bukkit.getLogger().warning("[Main] Failed to load " + file.getName());
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


    public void selectItem(Player player, String item) {
        for (PlayerData itemSelection : selections) {
            if (itemSelection.getPlayer().equals(player)) {
                itemSelection.setItem(item.toUpperCase());
                break;
            }
        }
        PlayerData pd = new PlayerData(player);
        pd.setItem(item);
        selections.add(pd);
    }

    public String getSelectedItem(Player player) {
        for (PlayerData itemSelection : selections) {
            if (itemSelection.getPlayer().equals(player)) {
                return itemSelection.getItem();
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
