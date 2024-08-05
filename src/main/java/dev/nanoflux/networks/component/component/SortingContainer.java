package dev.nanoflux.networks.component.component;

import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.component.module.Acceptor;
import dev.nanoflux.networks.component.module.Supplier;
import dev.nanoflux.config.util.exceptions.InvalidNodeException;
import dev.nanoflux.networks.utils.BlockLocation;
import dev.nanoflux.networks.utils.NamespaceUtils;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SortingContainer extends NetworkComponent implements Acceptor, Supplier {

    public static ComponentType type;
    public ComponentType type() {
        return type;
    }

    private String[] filters;
    private int acceptorPriority = 10;
    private int supplierPriority = 0;

    public static SortingContainer create(BlockLocation pos, PersistentDataContainer container) {
        if (container == null) return new SortingContainer(pos, new String[0], 10);
        return new SortingContainer(pos,
                Objects.requireNonNullElse(container.get(NamespaceUtils.FILTERS.key(), PersistentDataType.STRING).split(","), new String[0]),
                Objects.requireNonNullElse(container.get(NamespaceUtils.ACCEPTOR_PRIORITY.key(), PersistentDataType.INTEGER), 10),
                Objects.requireNonNullElse(container.get(NamespaceUtils.SUPPLIER_PRIORITY.key(), PersistentDataType.INTEGER), 0)
        );
    }

    public SortingContainer(BlockLocation pos, String[] filters, int acceptorPriority) {
        super(pos);
        this.filters = filters;
        this.acceptorPriority = acceptorPriority;
    }

    public SortingContainer(BlockLocation pos, String[] filters, int acceptorPriority, int supplierPriority) {
        super(pos);
        this.filters = filters;
        this.acceptorPriority = acceptorPriority;
        this.supplierPriority = supplierPriority;
    }

    private static ItemStack item(Material material) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        try {
            meta.displayName(Main.lang.getItemName("component." + type.tag()));
            meta.lore(Main.lang.getItemLore("component." + type.tag()));
            meta.getPersistentDataContainer().set(NamespaceUtils.FILTERS.key(), PersistentDataType.STRING, ",");
        } catch (InvalidNodeException e) {
            throw new RuntimeException(e);
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(NamespaceUtils.COMPONENT.key(), PersistentDataType.STRING, type.tag());
        stack.setItemMeta(meta);
        return stack;
    }

    public static ComponentType register() {
        type = ComponentType.register(
                SortingContainer.class,
                "sorting",
                Component.text("Sorting Container"),
                false,
                true,
                true,
                false,
                SortingContainer::create,
                SortingContainer::item
        );
        return type;
    }

    @Override
    public boolean accept(@Nonnull ItemStack stack) {
        return Arrays.stream(filters).anyMatch(stack.getType().toString()::equalsIgnoreCase);
    }

    public int acceptorPriority() {
        return acceptorPriority;
    }

    /**
     *
     */
    @Override
    public void incrementAcceptorPriority() {
        acceptorPriority++;
    }

    /**
     *
     */
    @Override
    public void decrementAcceptorPriority() {
        acceptorPriority--;
    }

    public int supplierPriority() {
        return supplierPriority;
    }

    public String[] filters() {
        return filters;
    }


    @Override
    public Map<String, Object> properties() {
        return new HashMap<>() {{
            put("acceptorPriority", acceptorPriority);
            put("supplierPriority", supplierPriority);
            put("filters", filters);
        }};
    }

    public void addFilter(String material) {
        filters = Arrays.copyOf(filters, filters.length + 1);
        filters[filters.length - 1] = material;
    }

    public void removeFilter(String material) {
        filters = ArrayUtils.removeElement(filters, material);
    }
}
