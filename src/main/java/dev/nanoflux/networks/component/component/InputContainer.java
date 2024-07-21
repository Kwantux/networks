package dev.nanoflux.networks.component.component;

import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.component.module.Donator;
import dev.nanoflux.config.util.exceptions.InvalidNodeException;
import dev.nanoflux.networks.utils.BlockLocation;
import dev.nanoflux.networks.utils.NamespaceUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InputContainer extends NetworkComponent implements Donator {

    public static ComponentType type;
    public ComponentType type() {
        return type;
    }

    private int range = 0;


    public static InputContainer create(BlockLocation pos, PersistentDataContainer container) {
        if (container == null) return new InputContainer(pos);
        return new InputContainer(pos,
                Objects.requireNonNullElse(container.get(NamespaceUtils.RANGE.key(), PersistentDataType.INTEGER), 0)
        );
    }

    public InputContainer(BlockLocation pos, int range) {
        super(pos);
        this.range = range;
    }

    public InputContainer(BlockLocation pos) {
        super(pos);
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
            meta.displayName(Main.lang.getItemName("component." + type.tag() + ".upgrade"));
            meta.lore(Main.lang.getItemLore("component." + type.tag() + ".upgrade"));
        } catch (InvalidNodeException e) {
            throw new RuntimeException(e);
        }
        stack.setItemMeta(meta);
        return stack;
    }

    public static ComponentType register() {
        type = ComponentType.register(
                InputContainer.class,
                "input",
                Component.text("Input Container"), 
                true, 
                false, 
                false, 
                false,
                InputContainer::create,
                InputContainer::blockItem,
                InputContainer::upgradeItem
        );
        return type;
    }

    @Override
    public List<ItemStack> donate() {
        return Arrays.asList(inventory().getContents());
    }

    @Override
    public int range() {
        return range;
    }

    @Override
    public void rangeUp() {
        this.range++;
    }

    @Override
    public Map<String, Object> properties() {
        return new HashMap<>() {
            {
                put(NamespaceUtils.RANGE.name, range);
            }
        };
    }
}
