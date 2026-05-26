package de.kwantux.networks.event;

import de.kwantux.networks.Main;
import de.kwantux.networks.Network;
import de.kwantux.networks.component.BasicComponent;
import de.kwantux.networks.component.BlockComponent;
import de.kwantux.networks.component.InstallableComponent;
import de.kwantux.networks.config.Config;
import de.kwantux.networks.utils.BlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static de.kwantux.networks.Main.*;

public class BlockBreakListener implements Listener {

    public BlockBreakListener(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    private static Item lastDrop;
    private static BlockComponent component;
    private static Material lastMaterial;
    private static boolean isComponent = false;

    @EventHandler (priority = EventPriority.HIGHEST)
    public void blockBreak(BlockBreakEvent event) {

        isComponent = false;
        component = dcu.componentAtLoadedBlock(event.getBlock());
        if (component == null) return;

        if (mgr.permissionUser(event.getPlayer(), component.network())) {

            ItemStack item = component.item();

            lastDrop = Bukkit.getServer().getWorld(component.pos().getWorld()).dropItem(component.pos().getBukkitLocation(), item);
            BlockLocation location = new BlockLocation(event.getBlock());
            mgr.removeComponent(location);
            lang.message(event.getPlayer(), "component.remove", location.toString());

            isComponent = true;
            lastMaterial = event.getBlock().getType();
        }
        else {
            lang.message(event.getPlayer(), "permission.user");
            event.setCancelled(true);
        }
    }


    @EventHandler (priority = EventPriority.HIGHEST)
    public void blockDrop(BlockDropItemEvent event) {
        if (!isComponent) return; // Was not a component block

        // If the player mined the component using silk touch, apply the component to the dropped block item and remove the installable item
        if (event.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.SILK_TOUCH) != 0) {

            // Don't do anything if the drop item is already dead (e.g. picked up by another player)
            if (lastDrop.isDead()) return;

            // Find the block drop in the list of dropped items
            for (Item blockDrop : event.getItems()) {

                ItemStack blockStack = blockDrop.getItemStack();

                // Check that this is actually the block drop
                if (blockStack.getAmount() == 1 && blockStack.getType().equals(lastMaterial)) {

                    // Replace the block drop with itself but with the component applied
                    blockDrop.setItemStack(component.item(blockStack));

                    // Remove the installable item that was dropped in the BlockBreakEvent
                    lastDrop.remove();
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(EntityExplodeEvent event) {

        List<Block> removeLater = new ArrayList<>();

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
                // Drop item next tick to avoid it being destroyed by the explosion
                Bukkit.getScheduler().runTaskLater(Main.instance, () ->
                    Bukkit.getServer().getWorld(component.pos().getWorld()).dropItem(component.pos().getBukkitLocation(), item)
                , 1);

                Network network = component.network();
                mgr.removeComponent(component.origin());
                List<UUID> users = network.users();
                users.add(network.owner());

                for (UUID uid : users) {
                    if (Bukkit.getPlayer(uid) != null) {
                        lang.message(Bukkit.getPlayer(uid), "component.exploded", network.name(), new BlockLocation(block).toString());
                    }
                }
            }

            else event.blockList().remove(block);
        }
    }
}
