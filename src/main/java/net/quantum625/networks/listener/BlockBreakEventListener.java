package net.quantum625.networks.listener;

import net.quantum625.config.lang.Language;
import net.quantum625.config.lang.LanguageController;
import net.quantum625.config.util.exceptions.InvalidNodeException;
import net.quantum625.networks.Main;
import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.Network;
import net.quantum625.networks.component.BaseComponent;
import net.quantum625.networks.component.InputContainer;
import net.quantum625.networks.component.SortingContainer;
import net.quantum625.networks.component.MiscContainer;
import net.quantum625.networks.data.Config;
import net.quantum625.networks.data.CraftingManager;
import net.quantum625.networks.utils.DoubleChestDisconnecter;
import net.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;

public class BlockBreakEventListener implements Listener {

    private NetworkManager net;
    private CraftingManager crafting;
    private Config config;
    private DoubleChestDisconnecter dcd;
    private LanguageController lang;


    public BlockBreakEventListener(Main main, CraftingManager craftingManager, DoubleChestDisconnecter doubleChestDisconnecter) {
        net = main.getNetworkManager();
        crafting = craftingManager;
        lang = main.getLanguage();
        dcd = doubleChestDisconnecter;
        this.config = config;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void blockBreak(BlockBreakEvent event) throws InvalidNodeException, SerializationException {

        for (Network network : net.listAll()) {
            for (BaseComponent component : network.getAllComponents()) {
                if (component.getPos().equals(new Location(event.getBlock()))) {
                    
                    dcd.disconnectChests(component.getPos());
                    
                    if (net.checkNetworkPermission(event.getPlayer(), network) > 1) {
                        if (component instanceof InputContainer) {
                            ItemStack inputContainer = crafting.getInputContainer(event.getBlock().getType());
                            Bukkit.getServer().getWorld(component.getPos().getDim()).dropItem(component.getPos().getBukkitLocation(), inputContainer);
                            event.setDropItems(false);
                        }
                        if (component instanceof SortingContainer) {

                            SortingContainer container = (SortingContainer) component;
                            ItemStack sortingContainer = crafting.getSortingContainer(event.getBlock().getType(), container.getItems());
                            Bukkit.getServer().getWorld(component.getPos().getDim()).dropItem(component.getPos().getBukkitLocation(), sortingContainer);
                            event.setDropItems(false);
                        }
                        if (component instanceof MiscContainer) {

                            ItemStack miscContainer = crafting.getMiscContainer(event.getBlock().getType());
                            Bukkit.getServer().getWorld(component.getPos().getDim()).dropItem(component.getPos().getBukkitLocation(), miscContainer);
                            event.setDropItems(false);
                        }

                        for (ItemStack stack : component.getInventory()) {
                            if (stack != null) {
                                Bukkit.getServer().getWorld(component.getPos().getDim()).dropItem(component.getPos().getBukkitLocation(), stack);
                            }
                        }
                        network.removeComponent(new Location(event.getBlock()));
                        lang.message(event.getPlayer(), "component.remove", new Location(event.getBlock()).toString());
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
