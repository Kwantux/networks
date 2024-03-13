package dev.nanoflux.networks.event;

import dev.nanoflux.config.lang.LanguageController;
import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Manager;
import dev.nanoflux.networks.Network;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.CraftingManager;
import dev.nanoflux.networks.utils.DoubleChestUtils;
import dev.nanoflux.networks.utils.BlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlockBreakListener implements Listener {

    private final Manager net;
    private final CraftingManager crafting;
    private final DoubleChestUtils dcu;
    private final LanguageController lang;


    public BlockBreakListener(Main main, CraftingManager craftingManager, DoubleChestUtils doubleChestDisconnecter) {
        main.getServer().getPluginManager().registerEvents(this, main);
        net = main.getNetworkManager();
        crafting = craftingManager;
        lang = main.getLanguage();
        dcu = doubleChestDisconnecter;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void blockBreak(BlockBreakEvent event) {

        for (Network network : net.getNetworks()) {
            for (NetworkComponent component : List.copyOf(network.components())) {
                if (component.pos().equals(new BlockLocation(event.getBlock()))) {
                    
                    dcu.disconnectChests(component.pos());
                    
                    if (net.permissionUser(event.getPlayer(), network)) {

                        ItemStack item = component.type.blockItem(event.getBlock().getType());
                        Bukkit.getServer().getWorld(component.pos().getWorld()).dropItem(component.pos().getBukkitLocation(), item);
                        event.setDropItems(false);

                        for (ItemStack stack : component.inventory()) {
                            if (stack != null) {
                                Bukkit.getServer().getWorld(component.pos().getWorld()).dropItem(component.pos().getBukkitLocation(), stack);
                            }
                        }

                        BlockLocation location = new BlockLocation(event.getBlock());
                        net.removeComponent(location);
                        lang.message(event.getPlayer(), "component.remove", new BlockLocation(event.getBlock()).toString());
                    }
                    else {
                        lang.message(event.getPlayer(), "permission.user");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
