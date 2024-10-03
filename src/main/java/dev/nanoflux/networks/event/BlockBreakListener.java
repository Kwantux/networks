package dev.nanoflux.networks.event;

import dev.nanoflux.config.lang.LanguageController;
import dev.nanoflux.networks.config.Config;
import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Manager;
import dev.nanoflux.networks.Network;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.utils.DoubleChestUtils;
import dev.nanoflux.networks.utils.BlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlockBreakListener implements Listener {

    private final Manager manager;
    private final DoubleChestUtils dcu;
    private final LanguageController lang;


    public BlockBreakListener(Main main, DoubleChestUtils doubleChestDisconnecter) {
        main.getServer().getPluginManager().registerEvents(this, main);
        manager = main.getNetworkManager();
        lang = main.getLanguage();
        dcu = doubleChestDisconnecter;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void blockBreak(BlockBreakEvent event) {

        for (Network network : manager.getNetworks()) {
            for (NetworkComponent component : List.copyOf(network.components())) {
                if (component.pos().equals(new BlockLocation(event.getBlock()))) {

                    dcu.disconnectChests(component.pos());

                    if (manager.permissionUser(event.getPlayer(), network)) {

                        ItemStack item = component.item();
                        Bukkit.getServer().getWorld(component.pos().getWorld()).dropItem(component.pos().getBukkitLocation(), item);
                        BlockLocation location = new BlockLocation(event.getBlock());
                        manager.removeComponent(location);
                        lang.message(event.getPlayer(), "component.remove", location.toString());

                    }
                    else {
                        lang.message(event.getPlayer(), "permission.user");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(EntityExplodeEvent event) {

        ArrayList<Block> removeLater = new ArrayList<>();

        for (Block block : event.blockList()) {
            if (manager.getComponent(new BlockLocation(block)) != null) {
                removeLater.add(block);
            }
        }

        for (Block block : removeLater) {
            if (!Config.blastProofComponents) {
                NetworkComponent component = manager.getComponent(new BlockLocation(block));
                assert component != null; // Was already checked when adding blocks to the list

                ItemStack item = component.item();
                Bukkit.getServer().getWorld(component.pos().getWorld()).dropItem(component.pos().getBukkitLocation(), item);

                event.blockList().remove(block);
                block.setType(Material.AIR);
                Network network = manager.getNetworkWithComponent(new BlockLocation(block));
                network.removeComponent(new BlockLocation(block));
                ArrayList<UUID> users = (ArrayList<UUID>) network.users();
                users.add(network.owner());

                for (UUID uid : users) {
                    if (Bukkit.getPlayer(uid) != null) {
                        lang.message(Bukkit.getPlayer(uid), "component.exploded", network.name(), new BlockLocation(block).toString());
                    }
                }
            }

            event.blockList().remove(block);
        }
    }
}
