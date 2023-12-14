package quantum625.networks.listener;

import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Manager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;


public class AutoSave implements Listener {

    private final Manager net;

    public AutoSave(Main main) {
        this.net = main.getNetworkManager();
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        net.saveData();
    }

}
