package net.quantum625.networks.listener;

import net.quantum625.config.lang.Language;
import net.quantum625.networks.Network;
import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.data.Config;
import net.quantum625.networks.utils.DoubleChestDisconnecter;
import net.quantum625.networks.utils.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class RightClickEventListener implements Listener {

    private NetworkManager net;
    private Language lang;
    private Config config;

    private DoubleChestDisconnecter dcd;

    public RightClickEventListener(NetworkManager networkManager, Language languageModule, Config config) {
        net = networkManager;
        lang = languageModule;
        this.config = config;

        dcd = new DoubleChestDisconnecter(net);
    }

    @EventHandler(priority= EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerUse(PlayerInteractEvent event){
        if (!event.isCancelled()) {
            Player p = event.getPlayer();
            Location pos = new Location(event.getClickedBlock());
            Network network = net.getSelectedNetwork(p);

            String componentType = net.getSelectedComponentType(p);

            if (componentType != null && net.getSelectedNetwork(p) != null) {

                if (network.getComponentByLocation(pos) != null) {
                    lang.message(p, "location.occupied");
                    return;
                }

                if (config.checkLocation(pos, "container")) {
                    if (componentType == "input_container") {
                        net.getSelectedNetwork(p).addInputContainer(pos);
                        dcd.checkChest(pos);
                        net.selectComponentType(p, null);
                        lang.message(p, "component.input.add", network.getID(), pos.toString());
                    }

                    if (componentType == "item_container") {
                        net.getSelectedNetwork(p).addItemContainer(pos, net.getSelectedItems(p));
                        dcd.checkChest(pos);
                        net.selectComponentType(p, null);
                        lang.message(p, "component.item.add", network.getID(), pos.toString());
                    }

                    if (componentType == "misc_container") {
                        net.getSelectedNetwork(p).addMiscContainer(pos);
                        dcd.checkChest(pos);
                        net.selectComponentType(p, null);
                        lang.message(p, "component.misc.add", network.getID(), pos.toString());
                    }
                } else {
                    lang.message(p, "component.invalid_block", pos.getBlock().getType().toString());
                }


            }
        }
    }
}
