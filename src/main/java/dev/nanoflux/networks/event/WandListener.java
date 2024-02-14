package dev.nanoflux.networks.event;

import dev.nanoflux.config.lang.LanguageController;
import dev.nanoflux.networks.CraftingManager;
import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Manager;
import dev.nanoflux.networks.Network;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.utils.BlockLocation;
import dev.nanoflux.networks.utils.NamespaceUtils;
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

import static dev.nanoflux.networks.Main.config;

public class WandListener implements Listener {

    private final Manager manager;
    private final CraftingManager crafting;
    private final LanguageController lang;
    
    
    public WandListener(Main plugin, CraftingManager crafting) {
        this.manager = plugin.getNetworkManager();
        this.crafting = crafting;
        this.lang = plugin.getLanguage();
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }



    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        BlockLocation l = null;
        if (event.getClickedBlock() != null) l = new BlockLocation(event.getClickedBlock());
        Action action = event.getAction();

        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }

        ItemStack wand = p.getInventory().getItemInMainHand();

        if (!wand.getType().equals(Material.AIR)) {
            if (wand.getItemMeta().getPersistentDataContainer().has(NamespaceUtils.WAND.key())) {

                event.setCancelled(true);

                if (!event.getHand().equals(EquipmentSlot.HAND)) return;

                NetworkComponent component = manager.getComponent(l);


                if (!p.isSneaking()) {
                    if (action.equals(Action.RIGHT_CLICK_BLOCK)) {

                        if (component == null) {
                            lang.message(p, "component.nocomponent");
                            return;
                        }

                        lang.message(p, "wand.info", manager.getNetworkWithComponent(l).name(), l.toString(), component.type().tag);
                        for (String key : component.properties().keySet()) {
                            lang.message(p, "component.info." + key, component.properties().get(key).toString());
                        }
                    }
                    return;
                }


                if (component == null) {
                    lang.message(p, "component.nocomponent");
                    return;
                }

                if (manager.permissionUser(p, manager.getNetworkWithComponent(l))) {
                    lang.message(p, "permission.user");
                    return;
                }

                if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                    //TODO: Open Config UI
                }
            }

            if (p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey("networks", "upgrade.range"), PersistentDataType.INTEGER)) {
                if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                    event.setCancelled(true);
                    if (manager.getComponent(l) != null) {
                        Network network = manager.getNetworkWithComponent(l);

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
