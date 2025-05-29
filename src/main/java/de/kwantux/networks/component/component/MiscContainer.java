package de.kwantux.networks.component.component;

import de.kwantux.networks.component.BlockComponent;
import de.kwantux.networks.component.ComponentType;
import de.kwantux.networks.component.module.Acceptor;
import de.kwantux.networks.component.module.Supplier;
import de.kwantux.networks.utils.BlockLocation;
import de.kwantux.networks.utils.NamespaceUtils;
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

public class MiscContainer extends BlockComponent implements Acceptor, Supplier {

    public static ComponentType type;
    public ComponentType type() {
        return type;
    }

    private int acceptorPriority = -20;
    private int supplierPriority = 5;

    public static @Nullable MiscContainer create(Origin origin, PersistentDataContainer container) {
        if (origin instanceof BlockLocation pos) {
            if (container == null) return new MiscContainer(pos);
            return new MiscContainer(pos,
                    Objects.requireNonNullElse(container.get(NamespaceUtils.ACCEPTOR_PRIORITY.key(), PersistentDataType.INTEGER), -20),
                    Objects.requireNonNullElse(container.get(NamespaceUtils.SUPPLIER_PRIORITY.key(), PersistentDataType.INTEGER), 5)
            );
        }
        return null;
    }

    public MiscContainer(BlockLocation pos, int acceptorPriority, int supplierPriority) {
        super(pos);
        this.acceptorPriority = acceptorPriority;
        this.supplierPriority = supplierPriority;
    }
    public MiscContainer(BlockLocation pos, int acceptorPriority) {
        super(pos);
        this.acceptorPriority = acceptorPriority;
    }
    public MiscContainer(BlockLocation pos) {
        super(pos);
    }

    private static Map<String, Object> defaultProperties = new HashMap<>();

    static {
        defaultProperties.put(NamespaceUtils.ACCEPTOR_PRIORITY.name, -20);
        defaultProperties.put(NamespaceUtils.SUPPLIER_PRIORITY.name, 5);
    }

    public static ComponentType register() {
        type = ComponentType.register(
                MiscContainer.class,
                "misc",
                Component.text("Miscellaneous Container"),
                false,
                true,
                true,
                false,
                true,
                MiscContainer::create,
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
            put(NamespaceUtils.ACCEPTOR_PRIORITY.name, acceptorPriority);
            put(NamespaceUtils.SUPPLIER_PRIORITY.name, supplierPriority);
        }};
    }


}
