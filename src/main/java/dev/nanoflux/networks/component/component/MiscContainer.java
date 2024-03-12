package dev.nanoflux.networks.component.component;

import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.component.module.Acceptor;
import dev.nanoflux.networks.component.module.Supplier;
import dev.nanoflux.networks.utils.BlockLocation;
import dev.nanoflux.networks.utils.NamespaceUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MiscContainer extends NetworkComponent implements Acceptor, Supplier {

    private int acceptorPriority = -20;
    private int supplierPriority = 5;

    public static MiscContainer create(BlockLocation pos, PersistentDataContainer container) {
        return new MiscContainer(pos,
                Objects.requireNonNullElse(container.get(NamespaceUtils.ACCEPTOR_PRIORITY.key(), PersistentDataType.INTEGER), -20),
                Objects.requireNonNullElse(container.get(NamespaceUtils.SUPPLIER_PRIORITY.key(), PersistentDataType.INTEGER), 5)
        );
    }

    public MiscContainer(BlockLocation pos, int acceptorPriority, int supplierPriority) {
        super(pos);
        this.acceptorPriority = acceptorPriority;
        this.supplierPriority = supplierPriority;
    }
    public MiscContainer(BlockLocation pos) {
        super(pos);
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
                MiscContainer::create,
                MiscContainer::blockItem,
                MiscContainer::upgradeItem
        );
        return type;
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
            put("acceptorPriority", acceptorPriority);
            put("supplierPriority", supplierPriority);
        }};
    }


}
