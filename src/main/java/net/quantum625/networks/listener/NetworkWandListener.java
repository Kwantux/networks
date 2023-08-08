package net.quantum625.networks.listener;

import net.quantum625.config.lang.Language;
import net.quantum625.config.lang.LanguageController;
import net.quantum625.config.util.exceptions.InvalidNodeException;
import net.quantum625.networks.Main;
import net.quantum625.networks.Network;
import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.component.*;
import net.quantum625.networks.data.CraftingManager;
import net.quantum625.networks.data.Config;
import net.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class NetworkWandListener implements Listener {

    private final Config config;
    private final NetworkManager net;
    private final LanguageController lang;
    private final CraftingManager crafting;

    public NetworkWandListener(Main main, CraftingManager craftingManager) {
        this.config = main.getConfiguration();
        this.net = main.getNetworkManager();
        this.lang = main.getLanguage();
        this.crafting = craftingManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) throws InvalidNodeException {
        Player p = event.getPlayer();
        Location l = null;
        if (event.getClickedBlock() != null) l = new Location(event.getClickedBlock());
        Action action = event.getAction();

        ItemStack wand = p.getInventory().getItemInMainHand();

        if (!wand.getType().equals(Material.AIR)) {
            if (wand.getItemMeta().getPersistentDataContainer().has(new NamespacedKey("networks", "wand"), PersistentDataType.INTEGER)) {

                event.setCancelled(true);

                if (!event.getHand().equals(EquipmentSlot.HAND)) return;

                int mode = wand.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("networks", "wand"), PersistentDataType.INTEGER);
                BaseComponent component = net.getComponentByLocation(l);


                if (!p.isSneaking()) {
                    if (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR)) {
                        if (!p.isSneaking()) {
                            mode++;
                            if (mode > 1) mode = 0;
                            //p.getInventory().setItemInMainHand(crafting.getNetworkWand(mode));
                            event.getItem().setItemMeta(crafting.getNetworkWand(mode).getItemMeta());
                            lang.message(p, "wand.mode", lang.getRaw("wand.mode."+mode));
                            return;
                        }
                    }
                    if (action.equals(Action.RIGHT_CLICK_BLOCK)) {

                        if (component == null) {
                            lang.message(p, "component.nocomponent");
                            return;
                        }

                        if (component instanceof InputContainer) {
                            lang.message(p, "wand.info.input", net.getNetworkWithComponent(l).getID(), l.toString());

                        }

                        if (component instanceof SortingContainer container) {
                            lang.message(p, "wand.info.sorting", net.getNetworkWithComponent(l).getID(), l.toString(), String.valueOf(container.getPriority()), Arrays.stream(container.getItems()).toList().toString());

                        }

                        if (component instanceof MiscContainer container) {
                            lang.message(p, "wand.info.misc", net.getNetworkWithComponent(l).getID(), l.toString(), String.valueOf(container.getPriority()));

                        }
                    }
                    return;
                }

                if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_AIR)) {
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
                            lang.message(p, "component.priority", String.valueOf(container.getPriority()));
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
                            lang.message(p, "component.priority", String.valueOf(container.getPriority()));
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
                            lang.message(p, "rangeupgrade.success", String.valueOf(tier), network.getID());
                        }
                        if (tier == config.getMaxRanges().length) {
                            lang.message(p, "rangeupgrade.last");
                            return;
                        }
                        if (upgradeTier < tier) {
                            lang.message(p, "rangeupgrade.alreadyupgraded", String.valueOf(tier));
                        }
                        if (upgradeTier > tier) {
                            lang.message(p, "rangeupgrade.unlockfirst", String.valueOf(tier));
                        }
                    }
                }
            }
        }
    }
}
