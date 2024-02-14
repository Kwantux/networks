package dev.nanoflux.networks.component.component;

import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.component.module.Acceptor;
import dev.nanoflux.networks.component.module.Supplier;
import dev.nanoflux.networks.utils.BlockLocation;
import dev.nanoflux.networks.utils.NamespaceUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SortingContainer extends NetworkComponent implements Acceptor, Supplier {

    private String[] filters;
    private int acceptorPriority = -10;
    private int supplierPriority = 0;

    public static SortingContainer create(BlockLocation pos, PersistentDataContainer container) {
        return new SortingContainer(pos, container.get(NamespaceUtils.FILTERS.key(), PersistentDataType.STRING).split(","));
    }

    public SortingContainer(BlockLocation pos, String[] filters) {
        super(pos);
        this.filters = filters;
    }

    private static ItemStack newBlockItem(Material material) {
        ItemStack stack = SortingContainer.blockItem(material);
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(NamespaceUtils.FILTERS.key(), PersistentDataType.STRING, ",");
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
                SortingContainer::newBlockItem,
                SortingContainer::upgradeItem
        );
        return type;
    }

    @Override
    public boolean accept(@Nonnull ItemStack stack) {
        return inventory().firstEmpty() != -1 && Arrays.stream(filters).anyMatch(stack.getType().toString()::equalsIgnoreCase);
    }

    public int acceptorPriority() {
        return acceptorPriority;
    }

    public int supplierPriority() {
        return supplierPriority;
    }


    @Override
    public Map<String, Object> properties() {
        return new HashMap<>() {{
            put("acceptorPriority", acceptorPriority);
            put("supplierPriority", supplierPriority);
            put("filters", filters);
        }};
    }
}
