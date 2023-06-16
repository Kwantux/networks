package net.quantum625.networks;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.quantum625.config.lang.Language;
import net.quantum625.config.lang.LanguageController;
import net.quantum625.networks.component.BaseComponent;
import net.quantum625.networks.component.SortingContainer;
import net.quantum625.networks.data.Config;
import net.quantum625.networks.data.JSONNetwork;
import net.quantum625.networks.utils.Location;
import net.quantum625.networks.utils.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public final class NetworkManager {

    private ArrayList<Network> networks = new ArrayList<Network>();

    private ArrayList<PlayerData> selections = new ArrayList<PlayerData>();

    private ArrayList<UUID> noticedPlayers = new ArrayList<UUID>();

    private Map<Location, Network> input_locations = new HashMap<Location, Network>();
    private Network console_selection = null;

    private int lastSave = 0;

    private Config config;
    private File dataFolder;
    private LanguageController lang;
    private Logger logger;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();;


    public NetworkManager(Main plugin) {
        this.dataFolder = plugin.getDataFolder();
        this.config = plugin.getConfiguration();
        this.lang = plugin.getLanguage();
        this.logger = plugin.getLogger();
    }

    public boolean add(String id, UUID owner) {
        if (this.getFromID(id) == null) {
            networks.add(new Network(id, owner, -1, config.getMaxRanges()[0]));
            return true;
        }
        return false;
    }

    public boolean delete(String id) {
        if (getFromID(id) != null) {
            networks.remove(getFromID(id));
            File file = new File(dataFolder, "networks/"+id+".json");
            file.delete();
            /* Network Archiving

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            File newFile = new File(dataFolder, "networks/archive/"+id+" ["+dtf.format(now)+"].json");
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            file.renameTo(newFile);
             */
            return true;
        }
        return false;
    }


    public boolean rename(String id, String newid) {
        Network network = getFromID(id);
        if (network == null) return false;
        if (getFromID(newid) != null) return false;
        network.setID(newid);
        File file = new File(dataFolder, "networks/"+id+".json");
        file.delete();
        saveData();
        return true;
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
                lang.message(player, "notice");
                noticedPlayers.add(player.getUniqueId());
            }
        }
    }


    public int checkNetworkPermission(CommandSender sender, Network network) {

        if (sender instanceof Player player) {

            if (player.hasPermission("networks.admin.foreign.owner")) {
                //logger.info("[Networks] Player has admin permission for foreign owner");
                return 2; // Server admin permission
            }
            if (player.hasPermission("networks.admin.foreign.user")) {
                //logger.info("[Networks] Player has admin permission for foreign user");
                return 1; // Server admin permission
            }

            if (network.getOwner().equals(player.getUniqueId())) {
                //logger.info("[Networks] Player is an owner");
                return 2; // Network Owner permission
            }
            if (listFromUser(player.getUniqueId()).contains(network)) {
                //logger.info("[Networks] Player is a user");
                return 1; // Network User permission
            }

            return 0; // No permission
        }

        else return 2; // Console command
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
        if (Bukkit.getServer().getCurrentTick() == lastSave) return;
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
                logger.warning("[Networks] Failed to save network file " + n.getId() + ".json");
                e.printStackTrace();
                logger.info(e.getStackTrace().toString());
            }
        }
        lastSave = Bukkit.getServer().getCurrentTick();
    }

    public void loadData() {
        if (networks.size() > 0) {
            logger.info("Network cache not empty, cleaning up..");
            networks = new ArrayList<>();
        }
        new File(dataFolder, "networks/").mkdir();
        File[] files = new File(dataFolder, "networks/").listFiles();
        logger.info("Loading "+files.length+" networks..");
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile() && file.getName().endsWith(".json")) {
                try {
                    Scanner scanner = new Scanner(file);
                    String json = "";
                    while (scanner.hasNext()) {
                        json += scanner.next();
                    }

                    networks.add(new Network(gson.fromJson(json, JSONNetwork.class)));
                    logger.info("["+(i+1)+"/"+files.length+"] Loaded " + file.getName());
                } catch (IOException e) {
                    logger.severe("Failed to load network " + file.getName());
                    throw new RuntimeException(e);
                }
            }
            else logger.info("["+(i+1)+"/"+files.length+"] Skipping non JSON file…");
        }
        logger.info("All networks loaded.");
    }




    public void selectNetwork(CommandSender sender, Network network) {
        if (sender instanceof Player) {
            for (PlayerData networkSelection : selections) {
                if (networkSelection.getPlayer().equals((Player) sender)) {
                    networkSelection.setNetwork(network);
                    break;
                }
            }
            PlayerData pd = new PlayerData((Player) sender);
            pd.setNetwork(network);
            selections.add(pd);
        }
        else {
            console_selection = network;
        }
    }

    public Network getSelectedNetwork(CommandSender sender) {
        if (sender instanceof Player) {
            for (PlayerData networkSelection : selections) {
                if (networkSelection.getPlayer().equals((Player) sender)) {
                    return networkSelection.getNetwork();
                }
            }
            return null;
        }
        else return console_selection;
    }


    public Network getNetworkWithComponent(Location location) {
        for (Network network : networks) {
            if (network.getComponentByLocation(location) != null) {
                return network;
            }
        }
        return null;
    }

    public BaseComponent getComponentByLocation(Location location) {
        for (Network network : networks) {
            if (network.getComponentByLocation(location) != null) {
                return network.getComponentByLocation(location);
            }
        }
        return null;
    }


    public SortingContainer getSortingContainerByLocation(Location location) {
        for (Network network : networks) {
            if (network.getComponentByLocation(location) != null) {
                return network.getSortingContainerByLocation(location);
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

}
