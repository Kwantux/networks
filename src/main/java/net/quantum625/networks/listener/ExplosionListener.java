package net.quantum625.networks.listener;

import net.quantum625.config.lang.LanguageController;
import net.quantum625.networks.Main;
import net.quantum625.networks.Network;
import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.component.InputContainer;
import net.quantum625.networks.component.MiscContainer;
import net.quantum625.networks.component.SortingContainer;
import net.quantum625.networks.component.BaseComponent;
import net.quantum625.networks.data.Config;
import net.quantum625.networks.data.CraftingManager;
import net.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class ExplosionListener implements Listener {

    private final Config config;
    private final LanguageController lang;
    private final NetworkManager net;
    private final CraftingManager craftingManager;

    public ExplosionListener(Main main, CraftingManager craftingManager) {
        this.config = main.getConfiguration();
        this.lang = main.getLanguage();
        this.net = main.getNetworkManager();
        this.craftingManager = craftingManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(EntityExplodeEvent event) {

        ArrayList<Block> removeLater = new ArrayList<>();

        for (Block block : event.blockList()) {

            if (net.getComponentByLocation(new Location(block)) != null) {
                removeLater.add(block);
            }
        }

        for (Block block : removeLater) {
            if (!config.blastProofComponents()) {
                BaseComponent component = net.getComponentByLocation(new Location(block));
                if (component instanceof InputContainer) {
                    Bukkit.getServer().getWorld(component.getPos().getDim()).dropItem(component.getPos().getBukkitLocation(), craftingManager.getInputContainer(block.getType()));
                }
                if (component instanceof SortingContainer) {
                    Bukkit.getServer().getWorld(component.getPos().getDim()).dropItem(component.getPos().getBukkitLocation(), craftingManager.getSortingContainer(block.getType(), ((SortingContainer) component).getItems()));
                }
                if (component instanceof MiscContainer) {
                    Bukkit.getServer().getWorld(component.getPos().getDim()).dropItem(component.getPos().getBukkitLocation(),craftingManager.getMiscContainer(block.getType()));
                }

                for (ItemStack stack : component.getInventory()) {
                    if (stack != null) {
                        Bukkit.getServer().getWorld(component.getPos().getDim()).dropItem(component.getPos().getBukkitLocation(), stack);
                    }
                }

                event.blockList().remove(block);
                block.setType(Material.AIR);
                Network network = net.getNetworkWithComponent(new Location(block));
                network.removeComponent(new Location(block));
                ArrayList<UUID> users = (ArrayList<UUID>) network.getUsers().clone();
                users.add(network.getOwner());

                for (UUID uid : users) {
                    if (Bukkit.getPlayer(uid).isOnline()) {
                        lang.message(Bukkit.getPlayer(uid), "component.exploded", network.getID(), new Location(block).toString());
                    }
                }
            }

            event.blockList().remove(block);
        }
    }
}
