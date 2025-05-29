package de.kwantux.networks.component;

import de.kwantux.config.util.exceptions.InvalidNodeException;
import de.kwantux.networks.Main;
import de.kwantux.networks.config.Config;
import de.kwantux.networks.utils.NamespaceUtils;
import de.kwantux.networks.utils.Origin;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class BasicComponent {

    public abstract ComponentType type();
    public abstract Origin origin();


    public abstract Map<String, Object> properties();

    /**
     * Fills missing properties with default values
     * In case crucial properties (without default values) are missing, returns false
     */
    public boolean isMissingData() {
        return false;
    }

    /**
     * @return The installable item for this component
     */
    public ItemStack item() {
        return item(type(), properties());
    }

    /**
     * @return The inventory of this component
     */
    public abstract Inventory inventory();

    /**
     * @return The installable item for this component
     */
    public static ItemStack item(ComponentType type, Map<String, Object> properties) {
        ItemStack stack = new ItemStack(Main.cfg.getComponentUpgradeMaterial());
        ItemMeta meta = stack.getItemMeta();
        try {
            meta.displayName(Main.lang.getItemName("component." + type.tag()));
            List<Component> lore = Main.lang.getItemLore("component." + type.tag());
            if (Config.propertyLore)
                for (Map.Entry<String, Object> entry : properties.entrySet()) {
                    String value = String.valueOf(entry.getValue());
                    if (entry.getValue() instanceof int[]) value = Arrays.toString((int[]) entry.getValue());
                    if (entry.getValue() instanceof long[]) value = Arrays.toString((long[]) entry.getValue());
                    if (entry.getValue() instanceof byte[]) value = Arrays.toString((byte[]) entry.getValue());
                    lore.add(Main.lang.getFinal("property." + entry.getKey()).append(Component.text(": " + value)));
                }
            meta.lore(lore);
        } catch (InvalidNodeException e) {
            throw new RuntimeException(e);
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(NamespaceUtils.COMPONENT.key(), PersistentDataType.STRING, type.tag());
        mapToContainer(data, properties);
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Copies all properties from the map to the persistent data container
     * @param container The persistent data container to edit
     * @param map The map of properties
     * @throws IllegalArgumentException Supported data types are String, Integer, Long, Double, Float, Short, Byte, Boolean, int[], long[], byte[]
     */
    private static void mapToContainer(PersistentDataContainer container, Map<String,Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                container.set(NamespaceUtils.key(key), PersistentDataType.STRING, (String) value);
            }
            else if (value instanceof Integer) {
                container.set(NamespaceUtils.key(key), PersistentDataType.INTEGER, (int) value);
            }
            else if (value instanceof Long) {
                container.set(NamespaceUtils.key(key), PersistentDataType.LONG, (long) value);
            }
            else if (value instanceof Double) {
                container.set(NamespaceUtils.key(key), PersistentDataType.DOUBLE, (double) value);
            }
            else if (value instanceof Float) {
                container.set(NamespaceUtils.key(key), PersistentDataType.FLOAT, (float) value);
            }
            else if (value instanceof Short) {
                container.set(NamespaceUtils.key(key), PersistentDataType.SHORT, (short) value);
            }
            else if (value instanceof Byte) {
                container.set(NamespaceUtils.key(key), PersistentDataType.BYTE, (byte) value);
            }
            else if (value instanceof Boolean) {
                container.set(NamespaceUtils.key(key), PersistentDataType.BYTE, (boolean) value ? (byte) 1 : (byte) 0);
            }
            else if (value instanceof int[]) {
                container.set(NamespaceUtils.key(key), PersistentDataType.INTEGER_ARRAY, (int[]) value);
            }
            else if (value instanceof long[]) {
                container.set(NamespaceUtils.key(key), PersistentDataType.LONG_ARRAY, (long[]) value);
            }
            else if (value instanceof byte[]) {
                container.set(NamespaceUtils.key(key), PersistentDataType.BYTE_ARRAY, (byte[]) value);
            }
            else {
                throw new IllegalArgumentException("Unsupported value type: " + value.getClass() + " for key: " + key + " and value: " + value + "\nPlease report this to the networks developers / developers of networks addons");
            }
        }
    }

}
