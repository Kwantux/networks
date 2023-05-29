package net.quantum625.networks.listener;

import net.quantum625.config.lang.Language;
import net.quantum625.networks.Network;
import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.component.*;
import net.quantum625.networks.data.CraftingManager;
import net.quantum625.networks.data.Config;
import net.quantum625.networks.utils.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class NetworkWandListener implements Listener {

    private Config config;
    private NetworkManager net;
    private Language lang;
    private CraftingManager crafting;

    public NetworkWandListener(Config config, NetworkManager net, Language languageModule, CraftingManager craftingManager) {
        this.config = config;
        this.net = net;
        this.lang = languageModule;
        this.crafting = craftingManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Location l = null;
        if (p == null) return;
        if (event.getClickedBlock() != null) l = new Location(event.getClickedBlock());
        Action action = event.getAction();

        ItemStack wand = p.getInventory().getItemInMainHand();

        if (!wand.getType().equals(Material.AIR)) {
            if (wand.getItemMeta().getPersistentDataContainer().has(new NamespacedKey("networks", "wand"), PersistentDataType.INTEGER)) {

                event.setCancelled(true);

                int mode = wand.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("networks", "wand"), PersistentDataType.INTEGER);
                BaseComponent component = net.getComponentByLocation(l);


                if (!p.isSneaking()) {
                    if (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR)) {
                        if (!p.isSneaking()) {
                            mode++;
                            if (mode > 1) mode = 0;
                            p.getInventory().setItemInMainHand(crafting.getNetworkWand(mode));
                            return;
                        }
                    }
                    if (action.equals(Action.RIGHT_CLICK_BLOCK)) {

                        if (component == null) {
                            lang.message(p, "component.nocomponent");
                            return;
                        }

                        if (component instanceof InputContainer) {
                            lang.message(p, "info.input", net.getNetworkWithComponent(l).getID(), l.toString());

                        }

                        if (component instanceof SortingContainer container) {
                            lang.message(p, "info.sorting", net.getNetworkWithComponent(l).getID(), l.toString(), container.getPriority()+"", container.getItems().toString());

                        }

                        if (component instanceof MiscContainer container) {
                            lang.message(p, "info.misc", net.getNetworkWithComponent(l).getID(), l.toString(), container.getPriority()+"");

                        }
                    }
                    return;
                }

                if (component == null) {
                    lang.message(p, "component.nocomponent");
                    return;
                }

                if (net.checkNetworkPermission(p, net.getNetworkWithComponent(l)) < 1) {
                    lang.message(p, "permission.user");
                    return;
                }

                if (action.equals(Action.RIGHT_CLICK_BLOCK)) {

                    if (mode == 0 && !p.getInventory().getItemInOffHand().getType().equals(Material.AIR) && net.getComponentByLocation(l) instanceof SortingContainer) {
                        net.getSortingContainerByLocation(l).addItem(p.getInventory().getItemInOffHand().getType().toString().toUpperCase());
                        lang.message(p, "component.sorting.setitem", l.toString(), p.getInventory().getItemInOffHand().getType().toString());
                    }
                    if (mode == 1) {
                        if (component instanceof BaseOutputContainer container) {
                            container.incrementPriority();
                            lang.message(p, "component.priority", container.getPriority()+"");
                        }
                    }
                }

                if (action.equals(Action.LEFT_CLICK_BLOCK)) {

                    if (mode == 0 && net.getComponentByLocation(l) instanceof SortingContainer && !p.getInventory().getItemInOffHand().getType().equals(Material.AIR) && p.isSneaking()) {
                        net.getSortingContainerByLocation(l).removeItem(p.getInventory().getItemInOffHand().getType().toString().toUpperCase());
                        lang.message(p, "component.sorting.removeitem", l.toString(), p.getInventory().getItemInOffHand().getType().toString());
                    }

                    if (mode == 1) {
                        if (component instanceof BaseOutputContainer container) {
                            container.decrementPriority();
                            lang.message(p, "component.priority", container.getPriority()+"");
                        }
                    }
                }
            }

            if (p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey("networks", "upgrade"), PersistentDataType.INTEGER)) {
                if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                    event.setCancelled(true);
                    if (net.getComponentByLocation(l) != null) {
                        BaseComponent component = net.getComponentByLocation(l);
                        Network network = net.getNetworkWithComponent(l);

                        int tier = 0;
                        for (int i = 0; i < config.getMaxRanges().length; i++) {
                            if (network.getMaxRange() >= config.getMaxRanges()[i]) tier = i+1;
                            else break;
                        }

                        int upgradeTier = p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey("networks", "upgrade"), PersistentDataType.INTEGER);

                        if (upgradeTier == tier) {
                            network.setMaxRange(config.getMaxRanges()[tier]);
                            ItemStack item = p.getInventory().getItemInMainHand();
                            item.setAmount(item.getAmount() - 1);
                            lang.message(p, "rangeupgrade.success", network.getID(), tier+"");
                        }
                        if (upgradeTier < tier) {
                            lang.message(p, "rangeupgrade.alreadyupgraded", tier+"");
                        }
                        if (upgradeTier > tier) {
                            lang.message(p, "rangeupgrade.unlockfirst", tier+"");
                        }
                        if (tier == config.getMaxRanges().length) {
                            lang.message(p, "rangeupgrade.last");
                        }
                    }
                }
            }
        }
    }
}
