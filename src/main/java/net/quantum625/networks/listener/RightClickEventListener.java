package net.quantum625.networks.listener;

import net.kyori.adventure.text.Component;
import net.quantum625.config.lang.LanguageController;
import net.quantum625.networks.Main;
import net.quantum625.networks.Network;
import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.component.ComponentType;
import net.quantum625.networks.data.Config;
import net.quantum625.networks.utils.DoubleChestUtils;
import net.quantum625.networks.utils.Location;
import org.bukkit.GameMode;
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

import java.util.logging.Logger;

public class RightClickEventListener implements Listener {

    private final Main plugin;

    private final NetworkManager net;
    private final LanguageController lang;
    private final Config config;

    private final Logger logger;

    private DoubleChestUtils dcu;

    public RightClickEventListener(Main main) {
        plugin = main;
        this.net = main.getNetworkManager();
        this.lang = main.getLanguage();
        this.config = main.getConfiguration();
        this.logger = main.getLogger();

        dcu = new DoubleChestUtils(net);
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

                if (componentType == null) {
                    logger.severe("Invalid component type: " + data.get(key, PersistentDataType.STRING));
                    return;
                }

                if (!config.checkLocation(pos, "container")) {
                    lang.message(p, "component.invalid_block", Component.translatable(event.getClickedBlock().getType().getBlockTranslationKey()));
                    return;
                }

                if (network == null) {
                    lang.message(p, "select.noselection");
                    return;
                }

                if (net.getComponentByLocation(pos) != null) {
                    lang.message(p, "location.occupied");
                    return;
                }

                switch (componentType) {
                    case INPUT -> {
                        network.addInputContainer(pos);
                        if (p.getGameMode() != GameMode.CREATIVE) stack.setAmount(stack.getAmount()-1);
                        dcu.checkChest(pos);

                        lang.message(p, "component.input.add", pos.toString(), network.getID());
                    }
                    case SORTING -> {
                        network.addItemContainer(pos, new String[0]);
                        if (p.getGameMode() != GameMode.CREATIVE) stack.setAmount(stack.getAmount()-1);
                        dcu.checkChest(pos);
                        lang.message(p, "component.sorting.add", pos.toString(), network.getID());
                    }
                    case MISC -> {
                        network.addMiscContainer(pos);
                        if (p.getGameMode() != GameMode.CREATIVE) stack.setAmount(stack.getAmount()-1);
                        dcu.checkChest(pos);
                        lang.message(p, "component.misc.add", pos.toString(), network.getID());
                    }
                }
            }

        }
    }
}
