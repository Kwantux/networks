package de.kwantux.networks.component.component;

import de.kwantux.networks.component.BlockComponent;
import de.kwantux.networks.component.module.Donator;
import de.kwantux.networks.component.util.ComponentType;
import de.kwantux.networks.utils.BlockLocation;
import de.kwantux.networks.utils.NamespaceUtils;
import de.kwantux.networks.utils.Origin;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InputContainer extends BlockComponent implements Donator {

    public static ComponentType type;
    public ComponentType type() {
        return type;
    }

    private int range = 0;


    public static @Nullable InputContainer create(Origin origin, PersistentDataContainer container) {
        if (origin instanceof BlockLocation pos) {
            if (container == null) return new InputContainer(pos);
            return new InputContainer(pos,
                    Objects.requireNonNullElse(container.get(NamespaceUtils.RANGE.key(), PersistentDataType.INTEGER), 0)
            );
        }
        return null;
    }

    public InputContainer(BlockLocation pos, int range) {
        super(pos);
        this.range = range;
    }

    public InputContainer(BlockLocation pos) {
        super(pos);
    }

    private static final Map<String, Object> defaultProperties = new HashMap<>();

    static {
        defaultProperties.put(NamespaceUtils.RANGE.name, 0);
    }

    public static ComponentType register() {
        type = ComponentType.register(
                InputContainer.class,
                "input",
                Component.text("Input Container"), 
                true, 
                false, 
                false, 
                false,
                true,
                InputContainer::create,
                defaultProperties
        );
        return type;
    }

    @Override
    public List<ItemStack> donate() {
        return Arrays.asList(inventory().getContents());
    }

    @Override
    public int range() {
        return range;
    }

    public void rangeUp() {
        this.range++;
    }

    @Override
    public Map<String, Object> properties() {
        return new HashMap<>() {
            {
                put(NamespaceUtils.RANGE.name, range);
            }
        };
    }
}
