package de.kwantux.networks.component.component;

import de.kwantux.networks.Network;
import de.kwantux.networks.component.BlockComponent;
import de.kwantux.networks.component.module.Acceptor;
import de.kwantux.networks.component.module.Supplier;
import de.kwantux.networks.component.util.ComponentType;
import de.kwantux.networks.utils.BlockLocation;
import de.kwantux.networks.utils.Origin;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.kwantux.networks.utils.NamespaceUtils.ACCEPTOR_PRIORITY;
import static de.kwantux.networks.utils.NamespaceUtils.SUPPLIER_PRIORITY;

public class FallbackContainer extends BlockComponent implements Acceptor, Supplier {

    public static ComponentType type;
    public ComponentType type() {
        return type;
    }

    private int acceptorPriority = -20;
    private int supplierPriority = 5;

    public static @Nullable FallbackContainer create(Origin origin, Network network, PersistentDataContainer container) {
        if (origin instanceof BlockLocation pos) {
            if (container == null) return new FallbackContainer(pos, network);
            return new FallbackContainer(pos, network,
                    Objects.requireNonNullElse(container.get(ACCEPTOR_PRIORITY.key, PersistentDataType.INTEGER), -20),
                    Objects.requireNonNullElse(container.get(SUPPLIER_PRIORITY.key, PersistentDataType.INTEGER), 5)
            );
        }
        return null;
    }

    public FallbackContainer(BlockLocation pos, Network network, int acceptorPriority, int supplierPriority) {
        super(pos, network);
        this.acceptorPriority = acceptorPriority;
        this.supplierPriority = supplierPriority;
    }
    public FallbackContainer(BlockLocation pos, Network network, int acceptorPriority) {
        super(pos, network);
        this.acceptorPriority = acceptorPriority;
    }
    public FallbackContainer(BlockLocation pos, Network network) {
        super(pos, network);
    }

    private static Map<String, Object> defaultProperties = new HashMap<>();

    static {
        defaultProperties.put(ACCEPTOR_PRIORITY.name, -20);
        defaultProperties.put(SUPPLIER_PRIORITY.name, 5);
    }

    public static ComponentType register() {
        type = ComponentType.register(
                FallbackContainer.class,
                "fallback",
                Component.text("Fallback Container"),
                false,
                true,
                true,
                false,
                true,
                FallbackContainer::create,
                defaultProperties
        );
        return type;
    }

    @Override
    public boolean accept(@Nonnull ItemStack stack) {
        return true;
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


    @Override
    public Map<String, Object> properties() {
        return new HashMap<>() {{
            put(ACCEPTOR_PRIORITY.name, acceptorPriority);
            put(SUPPLIER_PRIORITY.name, supplierPriority);
        }};
    }


}
