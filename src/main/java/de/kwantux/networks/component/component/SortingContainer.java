package de.kwantux.networks.component.component;

import de.kwantux.networks.component.module.Acceptor;
import de.kwantux.networks.component.module.Supplier;
import de.kwantux.networks.utils.NamespaceUtils;
import de.kwantux.networks.component.ComponentType;
import de.kwantux.networks.component.NetworkComponent;
import de.kwantux.networks.utils.BlockLocation;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.inventory.ItemStack;
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

    private static Map<String, Object> defaultProperties = new HashMap<>();

    static {
        defaultProperties.put(NamespaceUtils.FILTERS.name, "");
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
                SortingContainer::create,
                defaultProperties
        );
        return type;
    }

    @Override
    public boolean fillMissingData() {
        if (filters == null)
            filters = new String[0];

        return pos != null;
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
            put(NamespaceUtils.ACCEPTOR_PRIORITY.name, acceptorPriority);
            put(NamespaceUtils.SUPPLIER_PRIORITY.name, supplierPriority);
            put(NamespaceUtils.FILTERS.name, String.join(",", filters));
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
