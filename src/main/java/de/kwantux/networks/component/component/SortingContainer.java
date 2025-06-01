package de.kwantux.networks.component.component;

import de.kwantux.networks.component.BlockComponent;
import de.kwantux.networks.component.module.Acceptor;
import de.kwantux.networks.component.module.Supplier;
import de.kwantux.networks.component.util.ComponentType;
import de.kwantux.networks.utils.BlockLocation;
import de.kwantux.networks.utils.NamespaceUtils;
import de.kwantux.networks.utils.Origin;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SortingContainer extends BlockComponent implements Acceptor, Supplier {

    public static ComponentType type;
    public ComponentType type() {
        return type;
    }

    private int[] filters;
    private int acceptorPriority;
    private int supplierPriority;

    public static @Nullable SortingContainer create(Origin origin, PersistentDataContainer container) {
        if (origin instanceof BlockLocation pos) {
            if (container == null) return new SortingContainer(pos, new int[0], 10, 0);

            int[] filters;

            try {
                filters = Objects.requireNonNull(container.get(NamespaceUtils.FILTERS.key(), PersistentDataType.INTEGER_ARRAY));        // Normal deserialization
            } catch (Exception e) {
                filters = convertLegacyFilters(                                                                                             // If deserialization fails, try legacy deserialization
                        Objects.requireNonNullElse(
                                Objects.requireNonNullElse(                                                                             // Try legacy deserialization
                                        container.get(NamespaceUtils.FILTERS.key(), PersistentDataType.STRING),
                                        null).split(","),
                                new String[0])                                                                                              // If legacy deserialization fails, use empty array
                );
            }

            // Remove all duplicates and 0 values from filters
            filters = Arrays.stream(filters).distinct().filter(i -> i != 0).toArray();

            return new SortingContainer(pos,
                    filters,
                    Objects.requireNonNullElse(container.get(NamespaceUtils.ACCEPTOR_PRIORITY.key(), PersistentDataType.INTEGER), 10),
                    Objects.requireNonNullElse(container.get(NamespaceUtils.SUPPLIER_PRIORITY.key(), PersistentDataType.INTEGER), 0)
            );
        }
        return null;
    }

    public SortingContainer(BlockLocation pos, int[] filters, int acceptorPriority) {
        super(pos);
        this.filters = filters;
        this.acceptorPriority = acceptorPriority;
        this.supplierPriority = 0;
    }

    public SortingContainer(BlockLocation pos, int[] filters, int acceptorPriority, int supplierPriority) {
        super(pos);
        this.filters = filters;
        this.acceptorPriority = acceptorPriority;
        this.supplierPriority = supplierPriority;
    }

    private static Map<String, Object> defaultProperties = new HashMap<>();

    static {
        defaultProperties.put(NamespaceUtils.FILTERS.name, new int[0]);
        defaultProperties.put(NamespaceUtils.ACCEPTOR_PRIORITY.name, 10);
        defaultProperties.put(NamespaceUtils.SUPPLIER_PRIORITY.name, 0);
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
                true,
                SortingContainer::create,
                defaultProperties
        );
        return type;
    }

    @Override
    public boolean isMissingData() {
        if (filters == null)
            filters = new int[0];
        return pos == null;
    }

    @Override
    public boolean accept(@Nonnull ItemStack stack) {
        int matId = stack.getType().ordinal();
        int metaHash = stack.getItemMeta().hashCode();
        for (int filter : filters) {
            if (matId == filter || metaHash == filter) return true;
        }
        return false;
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

    public int[] filters() {
        return filters;
    }


    @Override
    public Map<String, Object> properties() {
        return new HashMap<>() {{
            put(NamespaceUtils.ACCEPTOR_PRIORITY.name, acceptorPriority);
            put(NamespaceUtils.SUPPLIER_PRIORITY.name, supplierPriority);
            put(NamespaceUtils.FILTERS.name, filters);
        }};
    }

    public void addFilter(int filter) {
        filters = Arrays.copyOf(filters, filters.length + 1);
        filters[filters.length - 1] = filter;
    }

    public void setFilters(int[] filters) {
        this.filters = filters;
    }

    public void removeFilter(int filter) {
        filters = ArrayUtils.removeElement(filters, filter);
    }

    public static int[] convertLegacyFilters(String[] filters) {
        int[] newFilters = new int[filters.length];
        for (int i = 0; i < filters.length; i++) {
            newFilters[i] = Objects.requireNonNullElse(Material.getMaterial(filters[i]), Material.AIR).ordinal();
        }
        return newFilters;
    }
}
