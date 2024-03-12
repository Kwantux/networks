package dev.nanoflux.networks.event;

import dev.nanoflux.config.lang.LanguageController;
import dev.nanoflux.config.util.exceptions.InvalidNodeException;
import dev.nanoflux.networks.Config;
import dev.nanoflux.networks.CraftingManager;
import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Manager;
import dev.nanoflux.networks.Network;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.component.component.InputContainer;
import dev.nanoflux.networks.component.component.MiscContainer;
import dev.nanoflux.networks.component.component.SortingContainer;
import dev.nanoflux.networks.component.module.Acceptor;
import dev.nanoflux.networks.utils.BlockLocation;
import dev.nanoflux.networks.utils.DoubleChestUtils;
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

public class WandListener implements Listener {

    private final Config config;
    private final Manager net;
    private final LanguageController lang;
    private final CraftingManager crafting;
    private final DoubleChestUtils dcu;

    public WandListener(Main plugin, CraftingManager craftingManager, DoubleChestUtils dcu) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.config = plugin.getConfiguration();
        this.net = plugin.getNetworkManager();
        this.lang = plugin.getLanguage();
        this.crafting = craftingManager;
        this.dcu = dcu;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) throws InvalidNodeException {
        Player p = event.getPlayer();
        BlockLocation l = null;
        if (event.getClickedBlock() != null) l = new BlockLocation(event.getClickedBlock());
        Action action = event.getAction();

        ItemStack wand = p.getInventory().getItemInMainHand();

        if (!wand.getType().equals(Material.AIR)) {
            if (wand.getItemMeta().getPersistentDataContainer().has(new NamespacedKey("networks", "wand"), PersistentDataType.INTEGER)) {

                event.setCancelled(true);

                if (!event.getHand().equals(EquipmentSlot.HAND)) return;

                int mode = wand.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("networks", "wand"), PersistentDataType.INTEGER);
                
                if (!p.isSneaking()) {
                    if (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR)) {
                        if (!p.isSneaking()) {
                            mode++;
                            if (mode > 1) mode = 0;
                            //p.getInventory().setItemInMainHand(crafting.getNetworkWand(mode));
                            event.getItem().setItemMeta(crafting.getNetworkWand(mode).getItemMeta());
                            lang.message(p, "wand.mode", lang.getRaw("wand.mode." + mode));
                            return;
                        }
                    }
                }

                Network network = net.getNetworkWithComponent(l);
                NetworkComponent component = net.getComponent(l);

                if (!p.isSneaking()) {
                    if (action.equals(Action.RIGHT_CLICK_BLOCK)) {

                        if (component == null) {
                            lang.message(p, "component.nocomponent");
                            return;
                        }

                        if (component instanceof InputContainer) {
                            lang.message(p, "wand.info.input", network.name(), l.toString());

                        }

                        if (component instanceof SortingContainer container) {
                            lang.message(p, "wand.info.sorting", network.name(), l.toString(), String.valueOf(container.acceptorPriority()), Arrays.stream(container.filters()).toList().toString());

                        }

                        if (component instanceof MiscContainer container) {
                            lang.message(p, "wand.info.misc", network.name(), l.toString(), String.valueOf(container.acceptorPriority()));

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

                if (!net.permissionUser(p, network)) {
                    lang.message(p, "permission.user");
                    return;
                }

                if (action.equals(Action.RIGHT_CLICK_BLOCK)) {

                    if (mode == 0 && !p.getInventory().getItemInOffHand().getType().equals(Material.AIR) && net.getComponent(l) instanceof SortingContainer) {
                        NetworkComponent c = net.getComponent(l);
                        if (c instanceof SortingContainer container) {
                            container.addFilter(p.getInventory().getItemInOffHand().getType().toString().toUpperCase());
                            lang.message(p, "component.sorting.setitem", l.toString(), p.getInventory().getItemInOffHand().getType().toString());
                        }
                    }
                    if (mode == 1) {
                        if (component instanceof Acceptor container) {
                            container.incrementAcceptorPriority();
                            lang.message(p, "component.priority", String.valueOf(container.acceptorPriority()));
                        }
                    }
                }

                if (action.equals(Action.LEFT_CLICK_BLOCK)) {

                    if (mode == 0 && net.getComponent(l) instanceof SortingContainer && !p.getInventory().getItemInOffHand().getType().equals(Material.AIR) && p.isSneaking()) {
                        NetworkComponent c = net.getComponent(l);
                        if (c instanceof SortingContainer container) {
                            container.removeFilter(p.getInventory().getItemInOffHand().getType().toString().toUpperCase());
                            lang.message(p, "component.sorting.removeitem", l.toString(), p.getInventory().getItemInOffHand().getType().toString());
                        }
                    }

                    if (mode == 1) {
                        if (component instanceof Acceptor container) {
                            container.decrementAcceptorPriority();
                            lang.message(p, "component.priority", String.valueOf(container.acceptorPriority()));
                        }
                    }
                }
            }

            if (p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey("networks", "upgrade.range"), PersistentDataType.INTEGER)) {
                if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                    event.setCancelled(true);
                    if (net.getComponent(l) != null) {
                        Network network = net.getNetworkWithComponent(l);

                        int tier = 0;
                        for (int i = 0; i < config.getMaxRanges().length; i++) {
                            if (network.properties().baseRange() >= config.getMaxRanges()[i]) tier = i+1;
                            else break;
                        }

                        int upgradeTier = p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey("networks", "upgrade.range"), PersistentDataType.INTEGER);

                        if (upgradeTier == tier) {
                            network.properties().baseRange(config.getMaxRanges()[tier]);
                            ItemStack item = p.getInventory().getItemInMainHand();
                            item.setAmount(item.getAmount() - 1);
                            lang.message(p, "rangeupgrade.success", String.valueOf(tier), network.name());
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