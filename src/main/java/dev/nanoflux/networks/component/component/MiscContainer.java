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

public class MiscContainer extends NetworkComponent implements Acceptor, Supplier {

    public MiscContainer(Location pos) {
        super(pos);
    }


    public static void register() {
        type = ComponentType.register(MiscContainer.class,  "misc", Component.text("Miscellaneous Container"), false, true, true, false);
    }


}
