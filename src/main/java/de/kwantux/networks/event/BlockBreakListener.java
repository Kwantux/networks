package de.kwantux.networks.event;

import de.kwantux.networks.Main;
import de.kwantux.networks.Network;
import de.kwantux.networks.component.BlockComponent;
import de.kwantux.networks.config.Config;
import de.kwantux.networks.utils.BlockLocation;
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
import java.util.UUID;

import static de.kwantux.networks.Main.*;

public class BlockBreakListener implements Listener {

    public BlockBreakListener(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void blockBreak(BlockBreakEvent event) {

        for (Network network : mgr.getNetworks()) {
            for (BlockComponent component : network.components().stream().filter(component -> component instanceof BlockComponent).map(component -> (BlockComponent) component).toList()) {
                if (component.pos().equals(new BlockLocation(event.getBlock()))) {

                    dcu.disconnectChests(component.pos());

                    if (mgr.permissionUser(event.getPlayer(), network)) {

                        ItemStack item = component.item();
                        Bukkit.getServer().getWorld(component.pos().getWorld()).dropItem(component.pos().getBukkitLocation(), item);
                        BlockLocation location = new BlockLocation(event.getBlock());
                        mgr.removeComponent(location);
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
            if (mgr.getComponent(new BlockLocation(block)) != null) {
                removeLater.add(block);
            }
        }

        for (Block block : removeLater) {
            if (!Config.blastProofComponents) {
                BlockComponent component = (BlockComponent) mgr.getComponent(new BlockLocation(block));
                assert component != null; // Was already checked when adding blocks to the list

                ItemStack item = component.item();
                Bukkit.getServer().getWorld(component.pos().getWorld()).dropItem(component.pos().getBukkitLocation(), item);

                event.blockList().remove(block);
                block.setType(Material.AIR);
                Network network = mgr.getNetworkWithComponent(new BlockLocation(block));
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
