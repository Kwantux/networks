package de.kwantux.networks.component;

import de.kwantux.networks.component.util.ComponentType;
import de.kwantux.networks.utils.NamespaceUtils;
import de.kwantux.networks.utils.Origin;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
     * @return The inventory of this component
     */
    public abstract Inventory inventory();



    /**
     * Copies all properties from the map to the persistent data container
     * @param container The persistent data container to edit
     * @param map The map of properties
     * @throws IllegalArgumentException Supported data types are String, Integer, Long, Double, Float, Short, Byte, Boolean, int[], long[], byte[]
     */
    public static void mapToContainer(PersistentDataContainer container, Map<String,Object> map) {
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
