package net.quantum625.networks.listener;

import net.kyori.adventure.text.Component;
import net.quantum625.config.lang.LanguageController;
import net.quantum625.networks.Main;
import net.quantum625.networks.Network;
import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.component.ComponentType;
import net.quantum625.networks.data.Config;
import net.quantum625.networks.utils.DoubleChestDisconnecter;
import net.quantum625.networks.utils.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.N;

public class RightClickEventListener implements Listener {

    private final Main plugin;

    private final NetworkManager net;
    private final LanguageController lang;
    private final Config config;

    private DoubleChestDisconnecter dcd;

    public RightClickEventListener(Main main) {
        plugin = main;
        this.net = main.getNetworkManager();
        this.lang = main.getLanguage();
        this.config = main.getConfiguration();

        dcd = new DoubleChestDisconnecter(net);
    }

    @EventHandler(priority= EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerUse(PlayerInteractEvent event){
        if (!event.isCancelled()) {
            Player p = event.getPlayer();
            Location pos = new Location(event.getClickedBlock());
            ItemStack stack = event.getItem();
            if (stack == null) return;
            ItemMeta meta = stack.getItemMeta();
            if (meta == null) return;
            PersistentDataContainer data = meta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(plugin, "component");

            if (data.has(key)) {

                event.setCancelled(true);

                ComponentType componentType = ComponentType.get(data.get(key, PersistentDataType.STRING));
                Network network = net.getSelectedNetwork(p);

                if (network == null) {
                    lang.message(p, "select.noselection");
                    return;
                }

                if (net.getComponentByLocation(pos) != null) {
                    lang.message(p, "location.occupied");
                    return;
                }

                if (!config.checkLocation(pos, "container")) {
                    lang.message(p, "component.invalid_block", Component.translatable(event.getClickedBlock().getType().getBlockTranslationKey()));
                    return;
                }

                if (componentType == null) return;

                switch (componentType) {
                    case INPUT -> {
                        network.addInputContainer(pos);
                        dcd.checkChest(pos);
                        lang.message(p, "component.input.add", pos.toString(), network.getID());
                    }
                    case SORTING -> {
                        network.addItemContainer(pos, new String[0]);
                        dcd.checkChest(pos);
                        lang.message(p, "component.sorting.add", pos.toString(), network.getID());
                    }
                    case MISC -> {
                        network.addMiscContainer(pos);
                        dcd.checkChest(pos);
                        lang.message(p, "component.misc.add", pos.toString(), network.getID());
                    }
                }
            }

        }
    }
}
