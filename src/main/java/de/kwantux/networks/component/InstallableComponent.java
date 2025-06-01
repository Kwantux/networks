package de.kwantux.networks.component;

import de.kwantux.config.util.exceptions.InvalidNodeException;
import de.kwantux.networks.Main;
import de.kwantux.networks.component.util.ComponentType;
import de.kwantux.networks.config.Config;
import de.kwantux.networks.utils.NamespaceUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
}
