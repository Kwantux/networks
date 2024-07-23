package dev.nanoflux.networks.component.component;

import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.config.util.exceptions.InvalidNodeException;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MiscContainer extends NetworkComponent implements Acceptor, Supplier {

    public static ComponentType type;
    public ComponentType type() {
        return type;
    }

    private int acceptorPriority = -20;
    private int supplierPriority = 5;

    public static MiscContainer create(BlockLocation pos, PersistentDataContainer container) {
        if (container == null) return new MiscContainer(pos);
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
    public MiscContainer(BlockLocation pos, int acceptorPriority) {
        super(pos);
        this.acceptorPriority = acceptorPriority;
    }
    public MiscContainer(BlockLocation pos) {
        super(pos);
    }

    protected static ItemStack item(Material material) {
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
                MiscContainer::item
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
