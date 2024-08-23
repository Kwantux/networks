package dev.nanoflux.networks.component.component;

import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.component.module.Acceptor;
import dev.nanoflux.networks.component.module.Supplier;
import dev.nanoflux.networks.utils.BlockLocation;
import dev.nanoflux.networks.utils.NamespaceUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
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

//    protected static ItemStack newItem(Material material) {
//        ItemStack stack = new ItemStack(material);
//        ItemMeta meta = stack.getItemMeta();
//        try {
//            meta.displayName(Main.lang.getItemName("component." + type.tag()));
//            meta.lore(Main.lang.getItemLore("component." + type.tag()));
//        } catch (InvalidNodeException e) {
//            throw new RuntimeException(e);
//        }
//        PersistentDataContainer data = meta.getPersistentDataContainer();
//        data.set(NamespaceUtils.COMPONENT.key(), PersistentDataType.STRING, type.tag());
//        stack.setItemMeta(meta);
//        return stack;
//    }
//
//    @Override
//    public ItemStack item(Material material) {
//        ItemStack stack = newItem(material);
//        ItemMeta meta = stack.getItemMeta();
//        PersistentDataContainer data = meta.getPersistentDataContainer();
//        data.set(NamespaceUtils.COMPONENT.key(), PersistentDataType.STRING, type.tag());
//        data.set(NamespaceUtils.ACCEPTOR_PRIORITY.key(), PersistentDataType.INTEGER, acceptorPriority);
//        data.set(NamespaceUtils.SUPPLIER_PRIORITY.key(), PersistentDataType.INTEGER, supplierPriority);
//        stack.setItemMeta(meta);
//        return stack;
//    }

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
