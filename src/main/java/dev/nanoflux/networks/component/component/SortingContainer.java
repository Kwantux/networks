package dev.nanoflux.networks.component.component;

import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.component.NetworkComponent;
import dev.nanoflux.networks.component.module.Acceptor;
import dev.nanoflux.networks.component.module.Donator;
import dev.nanoflux.networks.component.module.Supplier;
import dev.nanoflux.networks.utils.Location;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class SortingContainer extends NetworkComponent implements Acceptor, Supplier {

    private String[] filters;
    public SortingContainer(Location pos, String[] filters) {
        super(pos);
        this.filters = filters;
    }

    public static void register() {
        type = ComponentType.register(SortingContainer.class, "sorting", Component.text("Sorting Container"), false, true, true, false);
    }

    @Override
    public boolean accept(@Nonnull ItemStack stack) {
        return inventory().firstEmpty() != -1 && Arrays.stream(filters).anyMatch(stack.getType().toString()::equalsIgnoreCase);
    }
}
