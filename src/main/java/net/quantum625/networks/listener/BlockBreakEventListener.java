package net.quantum625.networks.listener;

import net.quantum625.config.lang.Language;
import net.quantum625.config.util.exceptions.InvalidNodeException;
import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.Network;
import net.quantum625.networks.component.BaseComponent;
import net.quantum625.networks.component.InputContainer;
import net.quantum625.networks.component.SortingContainer;
import net.quantum625.networks.component.MiscContainer;
import net.quantum625.networks.data.Config;
import net.quantum625.networks.utils.DoubleChestDisconnecter;
import net.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
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
    private Config config;
    private DoubleChestDisconnecter dcd;
    private Language lang;


    public BlockBreakEventListener(NetworkManager networkManager, Config config, DoubleChestDisconnecter doubleChestDisconnecter, Language languageModule) {
        net = networkManager;
        lang = languageModule;
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

                            ItemStack inputContainer = new ItemStack(event.getBlock().getType());
                            ItemMeta meta = inputContainer.getItemMeta();
                            meta.setDisplayName(lang.getRaw("input"));
                            meta.setLore(lang.getList("input"));
                            PersistentDataContainer data = meta.getPersistentDataContainer();
                            data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "input");
                            inputContainer.setItemMeta(meta);
                            Bukkit.getServer().getWorld(component.getPos().getDim()).dropItem(component.getPos().getBukkitLocation(), inputContainer);
                            event.setDropItems(false);
                        }
                        if (component instanceof SortingContainer) {

                            String items = "";
                            List<String> itemslist = new ArrayList<String>();
                            itemslist.addAll(0, lang.getList("sorting"));
                            for (String item : ((SortingContainer) component).getItems()) {
                                items += item + ",";
                                itemslist.add("§r§f"+item);
                            }

                            ItemStack sortingContainer = new ItemStack(event.getBlock().getType());
                            ItemMeta meta = sortingContainer.getItemMeta();
                            meta.setDisplayName(lang.getRaw("sorting"));
                            meta.setLore(itemslist);
                            PersistentDataContainer data = meta.getPersistentDataContainer();
                            data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "sorting");
                            data.set(new NamespacedKey("networks", "filter_items"), PersistentDataType.STRING, items);
                            sortingContainer.setItemMeta(meta);
                            Bukkit.getServer().getWorld(component.getPos().getDim()).dropItem(component.getPos().getBukkitLocation(), sortingContainer);
                            event.setDropItems(false);
                        }
                        if (component instanceof MiscContainer) {

                            ItemStack miscContainer = new ItemStack(event.getBlock().getType());
                            ItemMeta meta = miscContainer.getItemMeta();
                            meta.setDisplayName(lang.getRaw("misc"));
                            meta.setLore(lang.getList("misc"));
                            PersistentDataContainer data = meta.getPersistentDataContainer();
                            data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "misc");
                            miscContainer.setItemMeta(meta);
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
