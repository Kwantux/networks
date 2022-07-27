package mc.portalcraft.autosort.data;

import mc.portalcraft.autosort.Autosort;
import mc.portalcraft.autosort.NetworkManager;
import mc.portalcraft.autosort.StorageNetwork;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Network {
    private Autosort main;
    private File file;
    public FileConfiguration config;

    public Network(Autosort main, StorageNetwork network) {
        this.main = main;
        this.file = new File(main.getDataFolder() + "/networks/", network.getID() + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);


        config.set("owner", network.getOwner().toString());
        config.createSection("containers");

        config.createSection("containers/input");
        for (int i = 0; i < network.getInputChests().size()-1; i++) {
            config.addDefault("containers/input", network.getInputChests().get(i).getPos().toString());
        }


    }
    public void save() {
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
