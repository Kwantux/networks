package dev.nanoflux.networks.component.component;

import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.component.module.Donator;
import dev.nanoflux.networks.utils.BlockLocation;
import dev.nanoflux.networks.utils.NamespaceUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InputContainer extends NetworkComponent implements Donator {

    private int range = 0;


    public static InputContainer create(BlockLocation pos, PersistentDataContainer container) {
        return new InputContainer(pos,
                Objects.requireNonNullElse(container.get(NamespaceUtils.RANGE.key(), PersistentDataType.INTEGER), 0)
        );
    }

    public InputContainer(BlockLocation pos, int range) {
        super(pos);
        this.range = range;
    }

    public InputContainer(BlockLocation pos) {
        super(pos);
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
                InputContainer::create,
                InputContainer::blockItem,
                InputContainer::upgradeItem
        );
        return type;
    }

    @Override
    public List<ItemStack> donate() {
        return Arrays.asList(inventory().getContents());
    }

    public int range() {
        return range;
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
