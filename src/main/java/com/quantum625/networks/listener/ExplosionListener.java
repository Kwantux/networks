package com.quantum625.networks.listener;

import com.quantum625.networks.Network;
import com.quantum625.networks.NetworkManager;
import com.quantum625.networks.commands.LanguageModule;
import com.quantum625.networks.component.InputContainer;
import com.quantum625.networks.component.MiscContainer;
import com.quantum625.networks.component.SortingContainer;
import com.quantum625.networks.component.BaseComponent;
import com.quantum625.networks.data.Config;
import com.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExplosionListener implements Listener {

    private Config config;
    private LanguageModule lang;
    private NetworkManager net;

    public ExplosionListener(Config config, LanguageModule lang, NetworkManager net) {
        this.config = config;
        this.lang = lang;
        this.net = net;
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
                if (!config.getEconomyState()) {
                    BaseComponent component = net.getComponentByLocation(new Location(block));
                    if (component instanceof InputContainer) {

                        ItemStack inputContainer = new ItemStack(block.getType());
                        ItemMeta meta = inputContainer.getItemMeta();
                        meta.setDisplayName(lang.getItemName("input"));
                        meta.setLore(lang.getItemLore("input"));
                        PersistentDataContainer data = meta.getPersistentDataContainer();
                        data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "input");
                        inputContainer.setItemMeta(meta);
                        Bukkit.getServer().getWorld(component.getPos().getDim()).dropItem(component.getPos().getBukkitLocation(), inputContainer);
                    }
                    if (component instanceof SortingContainer) {

                        String items = "";
                        List<String> itemslist = new ArrayList<String>();
                        itemslist.addAll(0, lang.getItemLore("sorting"));
                        for (String item : ((SortingContainer) component).getItems()) {
                            items += item + ",";
                            itemslist.add("§r§f" + item);
                        }

                        ItemStack sortingContainer = new ItemStack(block.getType());
                        ItemMeta meta = sortingContainer.getItemMeta();
                        meta.setDisplayName(lang.getItemName("sorting"));
                        meta.setLore(itemslist);
                        PersistentDataContainer data = meta.getPersistentDataContainer();
                        data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "sorting");
                        data.set(new NamespacedKey("networks", "filter_items"), PersistentDataType.STRING, items);
                        sortingContainer.setItemMeta(meta);
                        Bukkit.getServer().getWorld(component.getPos().getDim()).dropItem(component.getPos().getBukkitLocation(), sortingContainer);
                    }
                    if (component instanceof MiscContainer) {

                        ItemStack miscContainer = new ItemStack(block.getType());
                        ItemMeta meta = miscContainer.getItemMeta();
                        meta.setDisplayName(lang.getItemName("misc"));
                        meta.setLore(lang.getItemLore("misc"));
                        PersistentDataContainer data = meta.getPersistentDataContainer();
                        data.set(new NamespacedKey("networks", "component_type"), PersistentDataType.STRING, "misc");
                        miscContainer.setItemMeta(meta);
                        Bukkit.getServer().getWorld(component.getPos().getDim()).dropItem(component.getPos().getBukkitLocation(), miscContainer);
                    }

                    for (ItemStack stack : component.getInventory()) {
                        if (stack != null) {
                            Bukkit.getServer().getWorld(component.getPos().getDim()).dropItem(component.getPos().getBukkitLocation(), stack);
                        }
                    }

                    event.blockList().remove(block);
                    block.setType(Material.AIR);
                }
                Network network = net.getNetworkWithComponent(new Location(block));
                network.removeComponent(new Location(block));
                ArrayList<UUID> users = (ArrayList<UUID>) network.getUsers().clone();
                users.add(network.getOwner());

                for (UUID uid : users) {
                    if (Bukkit.getPlayer(uid).isOnline()) {
                        lang.returnMessage(Bukkit.getPlayer(uid), "component.exploded", network, new Location(block));
                    }
                }
            }

            event.blockList().remove(block);
        }
    }
}
