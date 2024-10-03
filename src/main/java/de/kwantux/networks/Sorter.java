package de.kwantux.networks;

import de.kwantux.networks.component.module.Acceptor;
import de.kwantux.networks.component.module.BaseModule;
import de.kwantux.networks.component.module.Donator;
import de.kwantux.networks.component.module.Requestor;
import de.kwantux.networks.component.module.Supplier;
import de.kwantux.networks.utils.FoliaUtils;
import de.kwantux.networks.config.Config;
import de.kwantux.networks.storage.InterThreadTransmissionController;
import de.kwantux.networks.component.module.*;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.logging.Level;

import static de.kwantux.networks.Main.logger;

public class Sorter {

    private static Integer[] ranges;
    private static boolean ittc = false;

    public static void setConfig(@Nonnull Config config) {
        ranges = config.getMaxRanges();
    }

    public static void transmit(@Nonnull ItemStack stack, BaseModule source, BaseModule target) {
        if (ittc) {
            InterThreadTransmissionController.transmit(stack, source, target);
        }
        else {
            source.inventory().remove(stack);
            target.inventory().addItem(stack);
        }
    }

    public static void donate(Network network, Donator donator) {
        List<? extends Acceptor> acceptors = network.acceptors();
        for (ItemStack item : donator.donate()) {
            if (item == null) continue;
            try {
                for (Acceptor acceptor : acceptors) {
                    if (acceptor.ready() && inDistance(network,donator, acceptor) && Acceptor.spaceFree(acceptor.inventory(), item) && acceptor.accept(item)) {
                        transmit(item, donator, acceptor);
                        break;
                    }
                }
            }
            catch (Throwable e) {
                if (!FoliaUtils.folia) logger.severe("Failed to sort item: " + e.getMessage());
                else logger.log(Level.FINER, "Failed to sort item (this will be regularly thrown if Folia is used): " + e.getMessage());
            } // Folia compatibility
        }
    }

    public static void request(Network network, Requestor requestor) {
        List<? extends Supplier> suppliers = network.suppliers();
        for (ItemStack item : requestor.requested()) {
            if (item == null) continue;
            try {
                for (Supplier supplier : suppliers) {
                    if (supplier.ready() && inDistance(network, requestor, supplier) && supplier.supply().contains(item)) {
                        transmit(item, supplier, requestor);
                        break;
                    }
                }
            }
            catch (Throwable e) {
                if (!FoliaUtils.folia) logger.severe("Failed to sort item: " + e.getMessage());
                else logger.log(Level.FINER, "Failed to sort item (this will be regularly thrown if Folia is used): " + e.getMessage());
            } // Folia compatibility
        }
    }

    public static boolean inDistance(Network network, ActiveModule active, PassiveModule passive) {
        return
                ranges[Math.min(active.range(), ranges.length - 1)] < 1 // If component range is set to infinity
                        || // or if
                        active.pos().getDistance(passive.pos()) <= // distance is smaller than
                        ranges[Math.min(active.range(), ranges.length - 1)] + network.range(); // component range + network range
    }
}
