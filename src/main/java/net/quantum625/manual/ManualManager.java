package net.quantum625.manual;

import net.quantum625.networks.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ManualManager {

    private static JavaPlugin plugin = Main.getProvidingPlugin(Main.class);
    private static final Logger logger = Bukkit.getLogger();


    public static Map<String, Manual> list = new HashMap<>();


    public static void register(Manual manual) {
        list.put(manual.getId(), manual);
        logger.info("[Manuals] Manual '"+manual.getId()+"' was registered by Plugin " + manual.getPlugin().getName());
    }


    public static Manual get(String id) {
        Manual result = list.get(id);
        if (result == null) {
            logger.warning("[Manuals] Manual '"+id+"' not found!");
            throw new RuntimeException("Manual '"+id+"' not found!");
        }
        return result;
    }

    public static void open(Player player, String id) {
        Manual manual = list.get(id);
        if (manual!= null) {
            manual.show(player);
        } else {
            logger.warning("[Manuals] Manual '"+id+"' not found!");
        }
    }
}
