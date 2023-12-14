package dev.nanoflux.networks.component.component;

import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.component.module.Donator;
import dev.nanoflux.networks.component.module.Supplier;
import dev.nanoflux.networks.utils.Location;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class InputContainer extends NetworkComponent implements Donator {

    public InputContainer(Location pos) {
        super(pos);
    }

    public static void register() {
        type = ComponentType.register(InputContainer.class,  "input", Component.text("Input Container"), true, false, false, false);
    }

    @Override
    public List<ItemStack> donate() {
        return Arrays.asList(inventory().getContents());
    }
}
