package de.kwantux.networks.component;

import de.kwantux.config.util.exceptions.InvalidNodeException;
import de.kwantux.networks.Main;
import de.kwantux.networks.component.util.ComponentType;
import de.kwantux.networks.component.util.FilterTranslator;
import de.kwantux.networks.config.Config;
import de.kwantux.networks.config.CraftingManager;
import de.kwantux.networks.utils.NamespaceUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static de.kwantux.networks.Main.cfg;

public abstract class InstallableComponent extends BasicComponent {

    /**
     * @return The installable item for this component
     */
    public ItemStack item() {
        return item(type(), properties());
    }

    /**
     * @return The installable item for this component
     */
    public ItemStack item(ItemStack baseItem) {
        return item(baseItem, type(), properties());
    }

    /**
     * @return The installable item for this component
     */
    public static ItemStack item(ComponentType type, Map<String, Object> properties) {
        return item(new ItemStack(Config.componentUpgradeMaterial), type, properties);
    }

    /**
     * @return A placable component block item
     */
    public static ItemStack item(ItemStack baseItem, ComponentType type, Map<String, Object> properties) {
        ItemMeta meta = baseItem.getItemMeta();
        meta.displayName(type.displayName());
        meta.lore(generateLore(properties, type));
        CraftingManager.setCustomModelDataForComponent(meta, type);
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(NamespaceUtils.COMPONENT.key, PersistentDataType.STRING, type.tag());
        mapToContainer(data, properties);
        baseItem.setItemMeta(meta);
        return baseItem;
    }

    public static List<Component> generateLore(Map<String, Object> properties, ComponentType type) {
        List<Component> lore = Main.lang.getItemLore("component." + type.tag());
        if (Config.propertyLore)
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                Component line = Main.lang.getFinal("property." + entry.getKey()).append(Component.text(": "));
                if (entry.getKey().equals("filters") && entry.getValue() instanceof int[] array) {
                    line = line.append(Component.text("["));
                    boolean first = true;
                    for (int filter : array) {
                        if (first) first = false;
                        else line = line.append(Component.text(", "));
                        line = line.append(FilterTranslator.translate(filter));
                    }
                    line = line.append(Component.text("]"));
                    lore.add(line);
                    continue;
                }
                if (entry.getKey().equals("range") && entry.getValue() instanceof Integer range) {
                    lore.add(Component.text(Config.ranges[Math.max(0, Math.min(range, Config.ranges.length - 1))]));
                    continue;
                }
                String value = String.valueOf(entry.getValue());
                if (entry.getValue() instanceof int[] array) value = Arrays.toString(array);
                if (entry.getValue() instanceof long[] array) value = Arrays.toString(array);
                if (entry.getValue() instanceof byte[] array) value = Arrays.toString(array);
                lore.add(line.append(Component.text(value)));
            }
        return lore;
    }
}
