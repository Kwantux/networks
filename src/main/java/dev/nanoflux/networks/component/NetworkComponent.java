package dev.nanoflux.networks.component;

import dev.nanoflux.config.util.exceptions.InvalidNodeException;
import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.utils.BlockLocation;
import dev.nanoflux.networks.utils.NamespaceUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public abstract class NetworkComponent {

    protected static ComponentType type;

    public static ComponentType type() {
        return type;
    }

    protected BlockLocation pos;

    protected NetworkComponent(BlockLocation pos) {
        this.pos = pos;
    }

    public BlockLocation pos() {
        return pos;
    }

    public abstract Map<String, Object> properties();

    public Inventory inventory() {
        Block block = Bukkit.getWorld(pos.getWorld()).getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        if (block.getState() instanceof InventoryHolder) {
            return ((InventoryHolder) block.getState()).getInventory();
        }
        // TODO: Remove Component from database
        return null;
    }

    
    
    protected static ItemStack blockItem(Material material) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        try {
            meta.displayName(Main.lang.getItemName("component." + type.tag()));
            meta.lore(Main.lang.getItemLore("component." + type.tag()));
        } catch (InvalidNodeException e) {
            throw new RuntimeException(e);
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(NamespaceUtils.COMPONENT.key(), PersistentDataType.STRING, type.tag());
        stack.setItemMeta(meta);
        return stack;
    }

    protected static ItemStack upgradeItem(Material material) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        try {
            meta.displayName(Main.lang.getItemName("component." + type.tag() + ".upgrade.name"));
            meta.lore(Main.lang.getItemLore("component." + type.tag() + ".upgrade.description"));
        } catch (InvalidNodeException e) {
            throw new RuntimeException(e);
        }
        stack.setItemMeta(meta);
        return stack;
    }
}
